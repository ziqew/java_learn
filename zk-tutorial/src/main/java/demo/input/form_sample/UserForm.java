package demo.input.form_sample;

public class UserForm {
	private RandomStringGenerator rsg = new RandomStringGenerator(4);

	private User user = new User();
	private String retypedPassword;
	private String captcha = rsg.getRandomString(), captchaInput;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getRetypedPassword() {
		return retypedPassword;
	}

	public void setRetypedPassword(String retypedPassword) {
		this.retypedPassword = retypedPassword;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getCaptchaInput() {
		return captchaInput;
	}

	public void setCaptchaInput(String captchaInput) {
		this.captchaInput = captchaInput;
	}
	
	public void regenerateCaptcha() {
		this.captcha = rsg.getRandomString();
	}

}
