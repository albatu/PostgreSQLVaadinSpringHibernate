package tr.com.albatu.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import tr.com.albatu.dal.UserDal;
import tr.com.albatu.entities.User;
@Route("/login")
public class LoginUI extends VerticalLayout {

	@Autowired
	private UserDal userDal;
	public LoginUI(UserDal userDal) {
		
		this.userDal = userDal;
		addClassName("login-rich-content");

		LoginForm loginForm = new LoginForm();
		loginForm.getElement().getThemeList().add("dark");
		//loginForm.setClassName(User.class);
		
		add(loginForm);
	}
}
