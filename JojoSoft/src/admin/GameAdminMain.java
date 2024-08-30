package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import game.Game;
import game.GameDAO;
import materials.JLableFactory;
import materials.JPanelFactory;
import picture.IconManager;
import user.User;

// 상단부 검색바 구성
class PnlToolBarAdmin extends JPanel {
	private JLabel lnlNickname;

	public PnlToolBarAdmin(PnlBasicAdmin pnlBasicAdmin) {
		setLayout(new BorderLayout());
		// pnlEast: 로그인,로그아웃, 사용자 닉네임 알려주는 패널
		JPanel pnlEast = new JPanel(new GridLayout(3, 0));
		lnlNickname = new JLabel(User.getCurUser().getUserNickName() + "님 환영합니다");
		JButton btnLogout = new JButton("로그아웃");
		btnLogout.addActionListener(pnlBasicAdmin);
		JButton btnCart = new JButton("장바구니");

		btnCart.addActionListener(pnlBasicAdmin);

		pnlEast.add(lnlNickname);
		pnlEast.add(btnLogout);
		pnlEast.add(btnCart);
		pnlEast.setBorder(new LineBorder(Color.RED));

		// 검색바랑 검색바 밑 버튼들 나누기 위한 패널생성
		JPanel pnlCenter = new JPanel(new GridLayout(2, 0));
		pnlCenter.setBorder(new LineBorder(Color.BLACK));

		// 검색바 패널
		JPanel pnlSerch = new JPanel();
		pnlSerch.setBorder(new LineBorder(Color.CYAN));
		JButton btnLogo = new JButton("JOJOSOFT");
		btnLogo.addActionListener(pnlBasicAdmin);
		JTextField tfSeach = new JTextField(20);
		JButton btnSeach = new JButton("검색");

		pnlSerch.add(btnLogo);
		pnlSerch.add(tfSeach);
		pnlSerch.add(btnSeach);

		// 상품 추가, 상품 수정
		JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlBtns.setBorder(new LineBorder(Color.green));
		JButton btnAddGame = new JButton("상품 추가");
		btnAddGame.addActionListener(pnlBasicAdmin);
		JButton btnUpdataGame = new JButton("상품 수정");

		pnlBtns.add(btnAddGame);
		pnlBtns.add(btnUpdataGame);

		pnlCenter.add(pnlSerch);
		pnlCenter.add(pnlBtns);

		add(pnlEast, BorderLayout.EAST);
		add(pnlCenter, BorderLayout.CENTER);

		setPreferredSize(new Dimension(900, 100));
	}

	public JLabel getLnlNickname() {
		return lnlNickname;
	}
}


public class GameAdminMain extends JFrame {
	private PnlBasicAdmin pnlBasicAdmin;

	public GameAdminMain() {
		pnlBasicAdmin = new PnlBasicAdmin();
		add(pnlBasicAdmin);
		setSize(920, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);

	}

//	public static void main(String[] args) {
//		new PnlTest().setVisible(true);
//
////		PictureDAO pictureDAO = new PictureDAO();
////		Path path = Paths.get("C:\\Users\\GGG\\Desktop\\춘식\\팀프", "bandai.png");
////		
////		try {
////			byte[] readAllBytes = Files.readAllBytes(path);
////			
////			int row = pictureDAO.insert(path.getFileName().toString(), readAllBytes);
////			System.out.println("삽입된 행 개수: " + row);
////			
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//	}
}
