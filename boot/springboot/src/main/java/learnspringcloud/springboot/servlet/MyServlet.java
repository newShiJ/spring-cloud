package learnspringcloud.springboot.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author chenmingming
 * @date 2018/9/14
 */
@WebServlet(
        name = "myServlet",
        urlPatterns = "/myServlet",
        initParams = {
                @WebInitParam(name = "myName", value = "CMM")
        }
)
public class MyServlet extends HttpServlet {

    String myName;

    @Override
    public void init(ServletConfig config) throws ServletException {
        myName = config.getInitParameter("myName");
        super.init();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        writer.write("<html><body>Hello,World " + myName + "</body></html>");
    }
}
