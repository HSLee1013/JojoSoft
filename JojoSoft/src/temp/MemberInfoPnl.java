package temp;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
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

import materials.DBUtil;
import user.User;
import user.UserDAO;

// 구매한 게임 이력을 확인할수 있는 탭
class ShoopingInfo extends JPanel {
	public ShoopingInfo() {

		setLayout(new GridLayout(5, 1));
		// 게임 아이디, 유저 아이디, 게임이름, 구매날짜, 당시 구매가, 결제 상태 순서로 리스트에 들어가있음
		List<List<String>> userInfoList = makeUserInfoList();

		if (userInfoList.size() == 0) {
			JLabel lbl = new JLabel("확인된 구매 이력이 없습니다.");
			lbl.setFont(new Font("굴림", Font.BOLD, 25));
			lbl.setHorizontalAlignment(SwingConstants.CENTER);
			add(lbl);
		} else {
			for (int i = 0; i < userInfoList.size(); i++) {

				JPanel pnl = new JPanel();

				makeLbl("게임 아이디 : ", userInfoList.get(i), 0, pnl);
				makeLbl("유저 아이디 : ", userInfoList.get(i), 1, pnl);
				makeLbl("게임 이름 : ", userInfoList.get(i), 2, pnl);
				makeLbl("구매 날짜 : ", userInfoList.get(i), 3, pnl);
				makeLbl("구매 가격 : ", userInfoList.get(i), 4, pnl);
				makeLbl("", userInfoList.get(i), 5, pnl);

				add(pnl);
			}
		}
	}

	private JLabel makeLbl(String detail, List<String> list, int index, JPanel pnl) {
		Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
		JLabel lbl = new JLabel(detail + list.get(index));
		lbl.setPreferredSize(new Dimension(lbl.getText().length() * 11 + 5, 40));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setBorder(border);
		pnl.add(lbl);
		return lbl;
	}

	private List<List<String>> makeUserInfoList() {

//		select game_id, A.user_nickname, D.game_name, order_date,round(D.game_price * (100 - C.order_discount) / 100, -2) as '당시 구매가' from `user` as A
//		   join `order` as B using (user_id)
//		    join `order_list` as C using (order_Id)
//		    join game as D using (game_id);

		List<List<String>> result = new ArrayList<>();

		String sql = "select game_id, A.user_id, D.game_name, order_date,round(D.game_price * (100 - C.order_discount) / 100, -2) as '당시 구매가', order_status from `user` as A\r\n"
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
					if (i != 4) {
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

// 정보수정 탭. 구현 중
class InfoChange extends JPanel implements ActionListener {

	private JLabel phoneLbl;
	private JLabel birthLbl;
	private JLabel nickNameLbl;
	private JLabel welcomeLbl;

	public InfoChange(JLabel jLabel) {
		this.welcomeLbl = jLabel;
		JPanel eastPnl = new JPanel();
		JPanel westPnl = new JPanel();
		eastPnl.setLayout(new GridLayout(6, 1));
		westPnl.setLayout(new GridLayout(4, 1));
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

		JButton pwChangeBtn = new JButton("비밀번호 변경하기");
		pwChangeBtn.addActionListener(this);
		JButton nickNameChangeBtn = new JButton("닉네임 변경하기");
		nickNameChangeBtn.addActionListener(this);
		JButton phoneNumChangeBtn = new JButton("전화번호 변경하기");
		phoneNumChangeBtn.addActionListener(this);
		JButton dateChangeBtn = new JButton("생년월일 변경하기");
		dateChangeBtn.addActionListener(this);

		eastPnl.add(idLbl);
		eastPnl.add(nickNameLbl);
		eastPnl.add(phoneLbl);
		eastPnl.add(birthLbl);
		eastPnl.add(gradeLbl);
		eastPnl.add(moneyLbl);

		westPnl.add(pwChangeBtn);
		westPnl.add(nickNameChangeBtn);
		westPnl.add(phoneNumChangeBtn);
		westPnl.add(dateChangeBtn);

		add(eastPnl, "East");
		add(westPnl, "west");
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

		if (e.getActionCommand().equals("비밀번호 변경하기")) {
			ChangeInfoPnl changeInfoPnl = new ChangeInfoPnl(e.getActionCommand(), this, welcomeLbl);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("닉네임 변경하기")) {
			ChangeInfoPnl changeInfoPnl = new ChangeInfoPnl(e.getActionCommand(), this, welcomeLbl);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("전화번호 변경하기")) {
			ChangeInfoPnl changeInfoPnl = new ChangeInfoPnl(e.getActionCommand(), this, welcomeLbl);
			changeInfoPnl.setVisible(true);
		} else if (e.getActionCommand().equals("생년월일 변경하기")) {
			ChangeInfoPnl changeInfoPnl = new ChangeInfoPnl(e.getActionCommand(), this, welcomeLbl);
			changeInfoPnl.setVisible(true);
		}
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

	public ChangeInfoPnl(String btnActionCommand, InfoChange infoChange, JLabel welcomeLbl) {
		this.infoChange = infoChange;
		this.welcomeLbl = welcomeLbl;
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
				int result = userDAO.changeUserInfo("닉네임 변경", User.getCurUser().getUserId(), null, textField.getText(), null,
						null);
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
			int result = userDAO.changeUserInfo("전화번호 변경", User.getCurUser().getUserId(), null, null, textField.getText(), null);
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
			int result = userDAO.changeUserInfo("생년월일 변경", User.getCurUser().getUserId(), null, null, null, textField.getText());
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
}

// 회원 정보 보기 버튼을 눌렀을 때 띄워주는 창
public class MemberInfoPnl extends JPanel {

	public MemberInfoPnl(JLabel jLabel) {
		JPanel shoopingInfo = new ShoopingInfo();
		JTabbedPane tabbedPane = new JTabbedPane();
		InfoChange infoChange = new InfoChange(jLabel);
		JScrollPane js = new JScrollPane(shoopingInfo);
		js.getVerticalScrollBar().setUnitIncrement(10);

		tabbedPane.addTab("회원 정보 수정", infoChange);
		tabbedPane.addTab("쇼핑 정보", js);
		add(tabbedPane);

	}
//	public static void main(String[] args) {
//		new MemberInfoPnl().setVisible(true);
//	}
}