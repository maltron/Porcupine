package net.nortlam.test;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import net.nortlam.porcupine.common.authenticator.Authenticator;
import net.nortlam.porcupine.common.authenticator.BasicAuthenticator;
import net.nortlam.porcupine.common.authenticator.FormAuthenticator;
import net.nortlam.porcupine.common.exception.AccessDeniedException;

/**
 *
 * @author Mauricio "Maltron" Leal
 */
public class TestCommons implements Serializable {

    private static final Logger LOG = Logger.getLogger(TestCommons.class.getName());

    public static final String OAUTH2_SERVER = "http://localhost:8080/server";

    // Information regarding the Sample Application
    public static final String SAMPLE_CLIENT_ID = "cG9yY3VwaW5lLWNsaWVudC05OTYyMWZhZC1iOWY3LTRlYzctYTM3MS05ZjU2NWUyM2I5MGU=";
    public static final String SAMPLE_SECRET = "cG9yY3VwaW5lLWNsaWVudC1zZWNyZXQtYjRmM2QyZmYtYmUxZS00ZjkxLWFiZWQtMjAyYWU1MTRhMDgy";
    public static final String SAMPLE_SCOPE = "EMAIL";
    public static final String SAMPLE_SCOPE_ID = "1";
    public static final String SAMPLE_REDIRECT_URI = "http%3A%2F%2Flocalhost%3A8080%2Fclient";
    public static final String SAMPLE_USERNAME = "maltron@gmail.com";
    public static final String SAMPLE_PASSWORD = "maltron";

    protected Client client;
    protected Response response;

    public TestCommons() {
    }

    protected void setupClient(Authenticator authenticator) throws AccessDeniedException {
        if (authenticator == null) {
            client = ClientBuilder.newClient();
        } else {
            client = ClientBuilder.newClient().register(authenticator);
        }
    }

    protected void print(String method, Response response) {
        // Response Code
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n",method);
        System.out.printf("HTTP/1.1 %d %s\n", response.getStatus(), response.getStatusInfo());
        StringBuilder builder = new StringBuilder();
        for (Entry<String, List<Object>> entry : response.getHeaders().entrySet()) {
            builder.delete(0, builder.length());
            for (int i = 0; i < entry.getValue().size(); i++) {
                builder.append(entry.getValue().get(i))
                        .append(i < entry.getValue().size() - 1 ? "," : "");
            }
            System.out.printf("%s: %s\n", entry.getKey(), builder.toString());
        }
        
        if(response.hasEntity())
            System.out.printf("\n%s\n\n",response.readEntity(String.class));
        System.out.printf("%s >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n",method);

//        LOG.log(Level.INFO, ">>> {0} Response: {1} {2}", new Object[]{
//            method != null ? method : "NULL",
//            response != null ? response.getStatus() : "NULL",
//            response != null ? response.getStatusInfo() : "NULL"
//        });
//        LOG.log(Level.INFO, ">>> {0} BODY: {1}", new Object[]{
//            method != null ? method : "NULL",
//            response != null ? (response.hasEntity()
//            ? response.readEntity(String.class) : "NULL") : "NULL"});
//        // Headers
//        StringBuilder builder = new StringBuilder();
//        MultivaluedMap<String, Object> headers = response.getHeaders();
//        for (Entry<String, List<Object>> entry : headers.entrySet()) {
//            List<Object> values = entry.getValue();
//            int size = values.size();
//            builder.delete(0, builder.length());
//            for (int i = 0; i < size; i++) {
//                builder.append(values.get(i)).append(i < size - 1 ? "," : "");
//            }
//
////            LOG.log(Level.INFO, ">>> Header({0}): {1}", new Object[] {
////                entry.getKey(), builder.toString()});
//            System.out.printf(">>> Header(%s): %s\n", entry.getKey(), builder.toString());
//        }
//
//        // Location
//        URI location = response.getLocation();
//        if (location != null) {
//            LOG.log(Level.INFO, ">>> {0} Location Scheme:{1} Scheme-Specific-Part:{2} "
//                    + "Authority:{3} User-info:{4} Host:{5} Port:{6} Path:{7} Query:{8} "
//                    + "Fragment:{9}", new Object[]{
//                        method,
//                        location.getScheme(), location.getSchemeSpecificPart(),
//                        location.getAuthority(), location.getUserInfo(),
//                        location.getHost(), location.getPort(), location.getPath(),
//                        location.getQuery(), location.getFragment()
//                    });
//            LOG.log(Level.INFO, ">>> {0} Authorization Code:{1}", new Object[]{
//                method != null ? method : "NULL", getCode(location)
//            });
//        } else {
//            LOG.log(Level.INFO, ">>> {0} Location NULL");
//        }

    }

    protected String getCode(URI location) {
        if (location == null) {
            return null;
        }
        if (location.getQuery() == null) {
            return null;
        }

        String code = null;
        for (String query : location.getQuery().split("&")) {
            if (query.contains("code")) {
                code = query.substring(query.indexOf("code=") + 5, query.length());
                break;
            }
        }

        return code;
    }

    protected URI uriAuthorize() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(OAUTH2_SERVER);
        buffer.append("/oauth2/authorize");

        return URI.create(buffer.toString());
    }

    protected URI uriToken() {
        StringBuilder builder = new StringBuilder();
        builder.append(OAUTH2_SERVER);
        builder.append("/oauth2/token");

        return URI.create(builder.toString());
    }

    protected String uriAllow() {
        StringBuilder builder = new StringBuilder();
        builder.append(OAUTH2_SERVER);
        builder.append("/oauth2/allow/{clientID}/{scope}");

        return builder.toString();
    }

    protected Authenticator authenticatorForm() {
        try {
            return new FormAuthenticator(uriToken(), SAMPLE_USERNAME, SAMPLE_PASSWORD);
        } catch (AccessDeniedException ex) {
            LOG.log(Level.SEVERE, "authenticatorForm() ACCESS DENIED EXCEPTION");
        }

        return null;
    }

    protected Authenticator authenticatorBasic() {
        return new BasicAuthenticator(SAMPLE_USERNAME, SAMPLE_PASSWORD);
    }
}
