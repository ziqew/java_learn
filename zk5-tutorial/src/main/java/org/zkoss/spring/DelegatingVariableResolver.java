package org.zkoss.spring;

/**
 * Das ist Notwendig solange in SecurityWebAppInit eine fehlerhafte Abhängigkeit besteht!
 * Ist ein Fehler in zkspring-security (3.0)
 * 
 * Dies ist kein Workaround, es ist grausam und sollte schnelstens von zkoss behoben werden. :-(
 * @author bbruhns
 *
 */
public class DelegatingVariableResolver {
	public static String RESOLVER_CLASS = "???";
}
