package admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import main.Login;
import materials.DataManager;
import materials.PnlGameList;

//메인 패널
public class PnlBasicAdmin extends JPanel implements ActionListener {
	private PnlToolBarAdmin pnlToolBarAdmin;
	private CardLayout cardLayout;
	private JPanel pnlContainer;
	private PnlGameList pnlGameList;
	private PnlAddGame pnlAddGame;
	private PnlModifyGame pnlModifyGame;
	private SearchPnlAdmin searchPnl;

	public PnlBasicAdmin() {
		DataManager.removeData("pnlBasic");
		DataManager.inputData("pnlBasic", this);
		pnlToolBarAdmin = new PnlToolBarAdmin(this);

		// CardLayout과 패널 컨테이너 설정
		cardLayout = new CardLayout();
		pnlContainer = new JPanel(cardLayout);
		// 메인 정보 패널 및 스크롤 패널 설정
		pnlGameList = new PnlGameList();
		pnlAddGame = new PnlAddGame();
		pnlModifyGame = new PnlModifyGame();
		searchPnl = new SearchPnlAdmin(pnlToolBarAdmin.getTfSearch().getText(), this, null);

		// CardLayout에 패널 추가
		pnlContainer.add(pnlGameList, "MainPanel");
		pnlContainer.add(pnlAddGame, "AddGame");
		pnlContainer.add(pnlModifyGame, "GameDetail");

		pnlContainer.add(searchPnl, "SearchPnl");

		setLayout(new BorderLayout());

		add(pnlToolBarAdmin, BorderLayout.NORTH);

		add(pnlContainer, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("JOJOSOFT")) {
			changeScreenToMain();
		} else if (e.getActionCommand().equals("상품 추가")) {
			pnlAddGame.update();
			cardLayout.show(pnlContainer, "AddGame");
		} else if (e.getActionCommand().equals("상품 수정")) {
			changeScreenToMain();
		} else if (e.getActionCommand().equals("로그아웃")) {
			// 기본적으로 내장된 윈도우 메소드를 통해 모든 창을 가져옴.
			// 반복문을 통하여 가져오는 모든 창들을 dispose(종료)시킴
			// 새로운 로그인 창을 만들어서 visible을 true로 변경함
			// import는 import java.awt.Window; 를 사용
			for (Window window : Window.getWindows()) {
				window.dispose();
			}
			new Login().setVisible(true);
		} else if (e.getActionCommand().equals("검색")) {

			reconstructionSearchPnl(pnlToolBarAdmin.getTfSearch().getText(), null);
			cardLayout.show(pnlContainer, "SearchPnl");

		} else if (e.getActionCommand().equals("검색 창 내부 검색")) {

			String saveStr = searchPnl.getSaveStr();
			reconstructionSearchPnl(saveStr, searchPnl.getTfField().getText());
		}
	}

	private void reconstructionSearchPnl(String str1, String str2) {
		searchPnl.removeAll();
		searchPnl.reconstruction(str1, this, str2);
		searchPnl.revalidate();
		searchPnl.repaint();
	}

	public void changeScreenToGameDetail() {
		pnlModifyGame.update();
		cardLayout.show(pnlContainer, "GameDetail");
	}

	public void changeScreenToMain() {
		pnlGameList.initOptions();
		pnlGameList.recreateOptionComponents();
		pnlGameList.update();
		cardLayout.show(pnlContainer, "MainPanel");
	}
}
