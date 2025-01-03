package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import materials.DBUtil;
import materials.DataManager;
import materials.Function;
import order.OrderDAO;
import order.OrderListDAO;
import user.DeleteUserDAO;
import user.User;
import user.UserDAO;
import user.UserService;

// 구매한 게임 이력을 확인할수 있는 탭
class ShoopingInfo extends JPanel {
	public ShoopingInfo() {
		DataManager.inputData("ShoopingInfo", this);
		reconstruction();
	}

	public void reconstruction() {
		// 게임 아이디, 유저 아이디, 게임이름, 구매날짜, 당시 구매가, 결제 상태 순서로 리스트에 들어가있음
		List<List<String>> userInfoList = makeUserInfoList();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		if (userInfoList.size() == 0) {
			JLabel lbl = new JLabel("확인된 구매 이력이 없습니다.");
			lbl.setFont(new Font("굴림", Font.BOLD, 25));
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			add(lbl);
		} else {

			for (int i = 0; i < userInfoList.size(); i++) {

				int x = 10;
				JPanel pnl = new JPanel();
				pnl.setPreferredSize(new Dimension(1500, 100));
				pnl.setLayout(null);

				JLabel listLbl = new JLabel("목록 " + (i + 1));
				listLbl.setBounds(5, 0, 100, 20);
				pnl.add(listLbl);
				JLabel gameIdLbl = makeLbl("게임 코드 : ", userInfoList.get(i), 0, pnl, x, 20);
				x = x + gameIdLbl.getSize().width + 10;
				JLabel userIdLbl = makeLbl("유저 아이디 : ", userInfoList.get(i), 1, pnl, x, 20);
				x = x + userIdLbl.getSize().width + 10;
				JLabel gameNameLbl = makeLbl("게임 이름 : ", userInfoList.get(i), 2, pnl, x, 20);
				x = x + gameNameLbl.getSize().width + 10;
				JLabel buyDateLbl = makeLbl("구매 날짜 : ", userInfoList.get(i), 3, pnl, x, 20);
				x = x + buyDateLbl.getSize().width + 10;
				JLabel priceLbl = makeLbl("구매 가격 : ", userInfoList.get(i), 4, pnl, x, 20);
				x = x + priceLbl.getSize().width + 10;
				JLabel priceInfoLbl = makeLbl("결제 상태 : ", userInfoList.get(i), 5, pnl, x, 20);
				x = x + priceInfoLbl.getSize().width + 10;
				if (priceInfoLbl.getText().equals("결제 상태 : 결제 X")) {
					JLabel lastLbl = makeLbl("장바구니를 확인하세요", null, 0, pnl, x, 20);
					lastLbl.setBackground(new Color(200, 200, 200));
					lastLbl.setOpaque(true);
				}

				add(pnl);
			}

			setPreferredSize(new Dimension(2000, userInfoList.size() * 90));
		}
	}

	private JLabel makeLbl(String detail, List<String> list, int index, JPanel pnl, int x, int y) {
		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);

		JLabel lbl = null;
		if (list == null) {
			lbl = new JLabel(detail);
		} else {
			lbl = new JLabel(detail + list.get(index));
		}
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setBorder(border);
		lbl.setBounds(x, y, (lbl.getText().length() * 10) + 50, 40);
		pnl.add(lbl);
		return lbl;
	}

	private List<List<String>> makeUserInfoList() {

//		select game_id, A.user_nickname, D.game_name, order_date,round(D.game_price * (100 - C.order_discount) / 100, -2) as '당시 구매가' from `user` as A
//		   join `order` as B using (user_id)
//		    join `order_list` as C using (order_Id)
//		    join game as D using (game_id);

		List<List<String>> result = new ArrayList<>();

		String sql = "select C.game_code, A.user_id, D.game_name, order_date,round(D.game_price * (100 - C.order_discount) / 100, -2) as '당시 구매가', order_status from `user` as A\r\n"
				+ "   join `order` as B using (user_id)\r\n" + "    join `order_list` as C using (order_Id)\r\n"
				+ "    join game as D using (game_id)\r\n" + "    where A.user_id = ?;";
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection("jojosoft");
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, User.getCurUser().getUserId());
			rs = stmt.executeQuery();

			while (rs.next()) {
				List<String> collect = new ArrayList<>();
				for (int i = 1; i < 7; i++) {
					if (i == 1) {
						collect.add(Function.changePWBasic(rs.getString(i)));
					} else if (i != 4 || i != 1) {
						if (i == 6) {
							if (rs.getString(i).equals("1")) {
								collect.add("결제 완료");
							} else {
								collect.add("결제 X");
							}
						}
						collect.add(rs.getString(i));
					} else {
						String before = rs.getString(i);
						String after = before.substring(0, 10);
						collect.add(after);
					}
				}
				result.add(collect);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "예외 발생. ShoopingInfo 클래스 확인");
		}
		return result;
	}
}

// 정보수정 탭.
class InfoChange extends JPanel implements ActionListener {

	private JLabel phoneLbl;
	private JLabel birthLbl;
	private JLabel nickNameLbl;
	private JLabel welcomeLbl;

	public InfoChange(JLabel jLabel) {
		reconstruction(jLabel);
	}

	public void reconstruction(JLabel jLabel) {
		this.welcomeLbl = jLabel;
		JPanel eastPnl = new JPanel();
		JPanel westPnl = new JPanel();
		
		// 위치를 맞추기 위해 빈 패널 생성 및 추가
		JPanel northEmptyPnl = new JPanel();
		northEmptyPnl.setPreferredSize(new Dimension(1, 450));
		
		
		eastPnl.setLayout(new GridLayout(7, 1));
		westPnl.setLayout(new GridLayout(5, 1));
		JLabel idLbl = new JLabel("아이디 : " + User.getCurUser().getUserId());
		idLbl.setFont(new Font("굴림", Font.BOLD, 18));
		nickNameLbl = new JLabel("닉네임 : " + User.getCurUser().getUserNickName());
		nickNameLbl.setFont(new Font("굴림", Font.BOLD, 18));
		phoneLbl = new JLabel("등록된 전화번호 : " + User.getCurUser().getUserPhoneNumber());
		phoneLbl.setFont(new Font("굴림", Font.BOLD, 18));
		birthLbl = new JLabel("등록된 생년월일 : " + User.getCurUser().getUserBirth());
		birthLbl.setFont(new Font("굴림", Font.BOLD, 18));
		JLabel gradeLbl = new JLabel("회원 등급 : " + User.getCurUser().getUserGrade());
		gradeLbl.setFont(new Font("굴림", Font.BOLD, 18));
		JLabel moneyLbl = new JLabel("현재까지 사용금액 : " + User.getCurUser().getUserUsedCash());
		moneyLbl.setFont(new Font("굴림", Font.BOLD, 18));
		JLabel chargeMoneyLbl = new JLabel("충전금액 : " + User.getCurUser().getUserChargeMoney());
		chargeMoneyLbl.setFont(new Font("굴림", Font.BOLD, 18));

		JButton pwChangeBtn = new JButton("비밀번호 변경하기");
		pwChangeBtn.addActionListener(this);
		JButton nickNameChangeBtn = new JButton("닉네임 변경하기");
		nickNameChangeBtn.addActionListener(this);
		JButton phoneNumChangeBtn = new JButton("전화번호 변경하기");
		phoneNumChangeBtn.addActionListener(this);
		JButton dateChangeBtn = new JButton("생년월일 변경하기");
		dateChangeBtn.addActionListener(this);
		JButton deleteAccountBtn = new JButton("회원 탈퇴");
		deleteAccountBtn.setOpaque(true);
		deleteAccountBtn.setBackground(new Color(255, 180, 180));
		deleteAccountBtn.addActionListener(this);

		eastPnl.add(idLbl);
		eastPnl.add(nickNameLbl);
		eastPnl.add(phoneLbl);
		eastPnl.add(birthLbl);
		eastPnl.add(gradeLbl);
		eastPnl.add(chargeMoneyLbl);
		eastPnl.add(moneyLbl);

		westPnl.add(pwChangeBtn);
		westPnl.add(nickNameChangeBtn);
		westPnl.add(phoneNumChangeBtn);
		westPnl.add(dateChangeBtn);
		westPnl.add(deleteAccountBtn);

		add(eastPnl, "East");
		add(westPnl, "west");
		add(northEmptyPnl, "North");
	}

	public JLabel getNickNameLbl() {
		return nickNameLbl;
	}

	public JLabel getPhoneLbl() {
		return phoneLbl;
	}

	public JLabel getBirthLbl() {
		return birthLbl;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ChangeInfoPnl changeInfoPnl = new ChangeInfoPnl(e.getActionCommand(), this, welcomeLbl);
		if (e.getActionCommand().equals("비밀번호 변경하기")) {
			changeInfoPnl.setBtnActionCommand("비밀번호 변경하기");
			changeInfoPnl.setLocationRelativeTo(this);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("닉네임 변경하기")) {
			changeInfoPnl.setBtnActionCommand("닉네임 변경하기");
			changeInfoPnl.setLocationRelativeTo(this);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("전화번호 변경하기")) {
			changeInfoPnl.setBtnActionCommand("전화번호 변경하기");
			changeInfoPnl.setLocationRelativeTo(this);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("생년월일 변경하기")) {
			changeInfoPnl.setBtnActionCommand("생년월일 변경하기");
			changeInfoPnl.setLocationRelativeTo(this);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("회원 탈퇴")) {
			changeInfoPnl.setBtnActionCommand("회원 탈퇴");
			int result = showDialog("탈퇴하시겠습니까?\n충전된 금액은 환불이 불가합니다.");
			if (result == 0) {
				UserDAO userDAO = new UserDAO();
				DeleteUserDAO deleteUserDAO = new DeleteUserDAO();
				OrderDAO orderDAO = new OrderDAO();
				OrderListDAO orderListDAO = new OrderListDAO();
				UserService service = new UserService(userDAO, deleteUserDAO, orderDAO, orderListDAO);
				User u = User.getCurUser();
				service.deleteUser(u);
				JOptionPane.showMessageDialog(this, "탈퇴가 완료되었습니다.");
				for (Window window : Window.getWindows()) {
					window.dispose();
				}
				new Login().setVisible(true);
			} else {
				JOptionPane.showMessageDialog(this, "취소되었습니다.");
			}
		}
	}
	
	private int showDialog(String str) {
		String[] options = { "탈퇴", "취소" };
		int choice = JOptionPane.showOptionDialog(this, str, "알림",  JOptionPane.DEFAULT_OPTION
				, JOptionPane.QUESTION_MESSAGE, null, options, null);

		return choice;
	}
}

// 개인 정보를 변경하려고 했을 때 띄워주는 다이얼로그 창
// 어떤 버튼을 눌렀는지에 따라서 창의 구성을 달리 하였음
class ChangeInfoPnl extends JDialog implements ActionListener {
	private JLabel questionLbl;
	private JButton btn;
	private JTextField textField;
	private JPasswordField pwTextField;
	private JLabel anotherQuestionLbl;
	private InfoChange infoChange;
	private JLabel welcomeLbl;
	private String btnActionCommand;
	public ChangeInfoPnl(String btnActionCommand, InfoChange infoChange, JLabel welcomeLbl) {
		this.infoChange = infoChange;
		this.welcomeLbl = welcomeLbl;
		this.btnActionCommand = btnActionCommand;
		JPanel pnl = new JPanel();
		pnl.setLayout(null);
		if (btnActionCommand.equals("비밀번호 변경하기")) {

			questionLbl = new JLabel("기존의 비밀번호를 입력해주세요");
			questionLbl.setBounds(70, 50, 250, 50);
			anotherQuestionLbl = new JLabel("비밀번호 : ");
			anotherQuestionLbl.setBounds(70, 84, 100, 50);
			pwTextField = new JPasswordField(10);
			pwTextField.setBounds(150, 100, 100, 20);
			btn = new JButton("변경하기");
			btn.setBounds(100, 150, 130, 30);
			btn.setActionCommand("기존 비밀번호");
			btn.addActionListener(this);

			pnl.add(questionLbl);
			pnl.add(anotherQuestionLbl);
			pnl.add(pwTextField);
			pnl.add(btn);

		} else if (btnActionCommand.equals("닉네임 변경하기")) {

			questionLbl = new JLabel("변경할 닉네임을 입력해주세요.");
			questionLbl.setBounds(75, 50, 250, 50);
			anotherQuestionLbl = new JLabel("변경할 닉네임 : ");
			anotherQuestionLbl.setBounds(60, 84, 150, 50);
			textField = new JTextField(10);
			textField.setBounds(170, 100, 100, 20);
			btn = new JButton("변경하기");
			btn.setBounds(100, 150, 130, 30);
			btn.setActionCommand("닉네임");
			btn.addActionListener(this);

			pnl.add(questionLbl);
			pnl.add(anotherQuestionLbl);
			pnl.add(textField);
			pnl.add(btn);

		} else if (btnActionCommand.equals("전화번호 변경하기")) {

			questionLbl = new JLabel("변경할 전화번호를 입력해주세요.");
			questionLbl.setBounds(70, 50, 250, 50);
			anotherQuestionLbl = new JLabel("전화번호 : ");
			anotherQuestionLbl.setBounds(60, 84, 150, 50);
			textField = new JTextField(10);
			textField.setBounds(170, 100, 100, 20);
			btn = new JButton("변경하기");
			btn.setBounds(100, 150, 130, 30);
			btn.setActionCommand("전화번호");
			btn.addActionListener(this);

			pnl.add(questionLbl);
			pnl.add(anotherQuestionLbl);
			pnl.add(textField);
			pnl.add(btn);

		} else if (btnActionCommand.equals("생년월일 변경하기")) {

			questionLbl = new JLabel("변경할 생년월일을 입력해주세요.");
			questionLbl.setBounds(70, 30, 250, 50);
			JLabel questionLbl2 = new JLabel("기존 : " + User.getCurUser().getUserBirth());
			questionLbl2.setBounds(105, 52, 250, 50);

			anotherQuestionLbl = new JLabel("생년월일 : ");
			anotherQuestionLbl.setBounds(60, 84, 150, 50);
			textField = new JTextField(10);
			textField.setBounds(170, 100, 100, 20);
			btn = new JButton("변경하기");
			btn.setBounds(100, 150, 130, 30);
			btn.setActionCommand("생년월일");
			btn.addActionListener(this);

			pnl.add(questionLbl);
			pnl.add(questionLbl2);
			pnl.add(anotherQuestionLbl);
			pnl.add(textField);
			pnl.add(btn);

		}

		setModal(true);
		add(pnl);

		setSize(360, 250);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	// 비밀번호, 닉네임, 폰 번호, 생년월일을 변경하려고 할 때의 버튼 액션리스너 모음
	@Override
	public void actionPerformed(ActionEvent e) {
		UserDAO userDAO = new UserDAO();

		if (e.getActionCommand().equals("기존 비밀번호")) {
			String pw = String.valueOf(pwTextField.getPassword());
			if (userDAO.findMember(User.getCurUser().getUserId(), pw) == null) {
				JOptionPane.showMessageDialog(this, "비밀번호를 잘못입력하셨습니다.");
			} else {
				JOptionPane.showMessageDialog(this, "확인 완료.");
				questionLbl.setText("변경하실 비밀번호를 입력해주세요.");
				anotherQuestionLbl.setText("비밀번호 : ");
				btn.setActionCommand("변경 비밀번호");
				pwTextField.setText("");
			}
		} else if (e.getActionCommand().equals("변경 비밀번호")) {
			String pw = String.valueOf(pwTextField.getPassword());
			if (pw.length() < 8 || pw.length() > 20) {
				JOptionPane.showMessageDialog(this, "비밀번호는 8~20 글자 사이로 입력하셔야 합니다.");
			} else {
				int result = userDAO.changeUserInfo("비밀번호 변경", User.getCurUser().getUserId(),
						String.valueOf(pwTextField.getPassword()), null, null, null);
				if (result == 1) {
					JOptionPane.showMessageDialog(this, "비밀번호 변경이 완료되었습니다.");
					this.setVisible(false);
					User.getCurUser().setUserPw(pw);
				} else if (result == 3) {
					JOptionPane.showMessageDialog(this, "기존의 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
				}
			}
		} else if (e.getActionCommand().equals("닉네임")) {
			if (textField.getText().length() < 2 || textField.getText().length() > 16) {
				JOptionPane.showMessageDialog(this, "닉네임은 2~16 글자 사이로 변경 가능합니다.");
			} else {
				int result = userDAO.changeUserInfo("닉네임 변경", User.getCurUser().getUserId(), null, textField.getText(),
						null, null);
				if (result == 1) {
					JOptionPane.showMessageDialog(this, "닉네임 변경이 완료되었습니다.");
					this.setVisible(false);
					User.getCurUser().setUserNickName(textField.getText());
					welcomeLbl.setText(User.getCurUser().getUserNickName() + "님 환영합니다!");
					infoChange.getNickNameLbl().setText("닉네임 : " + User.getCurUser().getUserNickName());
				} else if (result == 3) {
					JOptionPane.showMessageDialog(this, "기존의 닉네임과 동일한 닉네임으로 변경할 수 없습니다.");
				} else if (result == 4) {
					JOptionPane.showMessageDialog(this, "이미 존재하는 닉네임 입니다.");
				}
			}
		} else if (e.getActionCommand().equals("전화번호")) {
			int result = userDAO.changeUserInfo("전화번호 변경", User.getCurUser().getUserId(), null, null,
					textField.getText(), null);
			if (result == 2) {
				JOptionPane.showMessageDialog(this, "전화번호 양식이 올바르지 않습니다. ex : 010-1234-5678");
			} else if (result == 1) {
				User.getCurUser().setUserPhoneNumber(textField.getText());
				infoChange.getPhoneLbl().setText("등록된 전화번호 : " + User.getCurUser().getUserPhoneNumber());
				JOptionPane.showMessageDialog(this, "전화번호 변경이 완료되었습니다.");
				this.setVisible(false);
			} else if (result == 3) {
				JOptionPane.showMessageDialog(this, "기존의 전화번호와 동일한 전화번호로 변경할 수 없습니다.");
			} else if (result == 4) {
				JOptionPane.showMessageDialog(this, "이미 가입되어 있는 전화번호 입니다.");
			}
		} else if (e.getActionCommand().equals("생년월일")) {
			int result = userDAO.changeUserInfo("생년월일 변경", User.getCurUser().getUserId(), null, null, null,
					textField.getText());
			if (result == 2) {
				JOptionPane.showMessageDialog(this, "생년월일 양식이 올바르지 않습니다. ex : 2024-08-27");
			} else if (result == 1) {
				User.getCurUser().setUserBirth(textField.getText());
				infoChange.getBirthLbl().setText("등록된 생년월일 : " + User.getCurUser().getUserBirth());
				JOptionPane.showMessageDialog(this, "생년월일 변경이 완료되었습니다.");
				this.setVisible(false);
			} else if (result == 3) {
				JOptionPane.showMessageDialog(this, "기존과 동일한 생년월일 입니다.");
			}
		}
	}

	public String getBtnActionCommand() {
		return btnActionCommand;
	}

	public void setBtnActionCommand(String btnActionCommand) {
		this.btnActionCommand = btnActionCommand;
	}
	
	
}

// 회원 정보 보기 버튼을 눌렀을 때 띄워주는 창
public class MemberInfoPnl extends JPanel {

	public MemberInfoPnl(JLabel jLabel) {
		reconstruction(jLabel);
	}

	public void reconstruction(JLabel jLabel) {
		DataManager.inputData("MemberInfoPnl", this);
		DataManager.inputData("jLabel", jLabel);

		setLayout(new BorderLayout()); // BorderLayout으로 레이아웃 설정
		JPanel shoopingInfo = new ShoopingInfo();
		// shoopingInfo.setPreferredSize(new Dimension(1000, 1000));
		JTabbedPane tabbedPane = new JTabbedPane();
		InfoChange infoChange = new InfoChange(jLabel);
		JScrollPane js = new JScrollPane(shoopingInfo);
		js.getVerticalScrollBar().setUnitIncrement(10);

		tabbedPane.addTab("회원 정보 수정", infoChange);
		tabbedPane.addTab("쇼핑 정보", js);

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// 선택된 탭 인덱스 가져오기
				int selectedIndex = tabbedPane.getSelectedIndex();

				// 특정 탭이 선택되었을 때의 행동 지정
				switch (selectedIndex) {
				case 1:
					ShoopingInfo shoinfo = ((ShoopingInfo) DataManager.getData("ShoopingInfo"));
					shoinfo.removeAll();
					shoinfo.reconstruction();
					shoinfo.revalidate();
					shoinfo.repaint();
					break;
				default:
					break;
				}
			}
		});
		add(tabbedPane);
		add(tabbedPane, BorderLayout.CENTER); // JTabbedPane을 중앙에 추가
	}
}
