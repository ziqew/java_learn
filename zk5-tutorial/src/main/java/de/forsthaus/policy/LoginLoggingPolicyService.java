package de.forsthaus.policy;

import java.io.Serializable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import de.forsthaus.gui.service.GuiLoginLoggingPolicService;

/**
 * This class is called from spring aop as an aspect and is for logging <br>
 * the Login of a user. It is configurated in the <br>
 * '/zkoss/src/main/resources/springSecurityContext.xml' <br>
 * Logs success and fails, sessionID, timestamp and remoteIP. <br>
 * 
 * @author bbruhns
 * 
 */
public class LoginLoggingPolicyService implements Serializable {

	private static final long serialVersionUID = 1L;

	private GuiLoginLoggingPolicService guiLoginLoggingPolicService;

	public LoginLoggingPolicyService() {
	}

	private void logAuthPass(Authentication authentication) {
		final String user = authentication.getName();
		final long userId = ((User) authentication.getPrincipal()).getUserId();
		final String clientAddress = convertClientAddress(authentication);
		final String sessionId = convertClientSessionId(authentication);

		getGuiLoginLoggingPolicService().logAuthPass(user, userId, clientAddress, sessionId);
	}

	private void logAuthFail(Authentication authentication) {
		final String user = authentication.getName();
		final String clientAddress = convertClientAddress(authentication);
		final String sessionId = convertClientSessionId(authentication);

		getGuiLoginLoggingPolicService().logAuthFail(user, clientAddress, sessionId);
	}

	public Authentication loginLogging(ProceedingJoinPoint call) throws Throwable {
		final Authentication authentication = (Authentication) call.getArgs()[0];

		final Authentication result;
		try {
			result = (Authentication) call.proceed();
		} catch (Exception e) {
			logAuthFail(authentication);
			throw e;
		}

		if (result != null) {
			logAuthPass(result);
		}

		return result;
	}

	private String convertClientAddress(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getRemoteAddress();
		} catch (ClassCastException e) {
			// securitycontext ist vom falschen Typ!
			return "<unbekannt>";
		}
	}

	private String convertClientSessionId(Authentication authentication) {
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
			return details.getSessionId();
		} catch (ClassCastException e) {
			// securitycontext ist vom falschen Typ!
			return "<unbekannt>";
		}
	}

	public GuiLoginLoggingPolicService getGuiLoginLoggingPolicService() {
		return guiLoginLoggingPolicService;
	}

	public void setGuiLoginLoggingPolicService(GuiLoginLoggingPolicService guiLoginLoggingPolicService) {
		this.guiLoginLoggingPolicService = guiLoginLoggingPolicService;
	}
}
