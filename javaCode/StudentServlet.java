import com.sun.net.httpserver.HttpServer;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

class StudentDB {
    private Connection conn;

    StudentDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/student"; 
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }
    }

  public int addStu(int id,String fname, String lname, String dob, String email, String mobNo, String branch,String gender,String city,String state, String address  ) {
    String sql = "INSERT INTO student_detail(id,fname,lname,dob,email,mobNo,branch,gender,city,state,address) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    int rows = 0;
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        stmt.setString(2, fname);
        stmt.setString(3, lname);
        stmt.setString(4, dob);
        stmt.setString(5, email);
        stmt.setString(6, mobNo);
        stmt.setString(7, branch);
        stmt.setString(8, gender);
        stmt.setString(9, city);
        stmt.setString(10, state);
        stmt.setString(11, address);


        rows = stmt.executeUpdate();
        System.out.println(rows + " Student(s) Inserted");
    } catch (SQLException e) {
        e.printStackTrace(); // print full error
    }
    return rows;
  }
   int deleteStu(int delid){
        int rows=0;
        String sql = "delete from student_detail where id = ?";
         try{ 
             PreparedStatement delstmt = conn.prepareStatement(sql);
            delstmt.setInt(1,delid);
            rows = delstmt.executeUpdate();
            System.out.println(rows+"    Student(s) Deleted");
    
         }
         catch(SQLException e){
                System.out.println(e.getMessage());
         }
         return rows;

    }

   int updStu(int uid, String ufname, String ulname, String udob, String uemail, String umobNo, 
           String ubranch, String ugender, String ucity, String ustate, String uaddress) {
    int rows = 0; 
    String sql = "UPDATE student_detail SET fname=?, lname=?, dob=?, email=?, mobNo=?, branch=?, gender=?, city=?, state=?, address=? WHERE id = ?";
    try (PreparedStatement updPre = conn.prepareStatement(sql)) {
        updPre.setString(1, ufname);
        updPre.setString(2, ulname);
        updPre.setString(3, udob);
        updPre.setString(4, uemail);
        updPre.setString(5, umobNo);
        updPre.setString(6, ubranch);
        updPre.setString(7, ugender);
        updPre.setString(8, ucity);
        updPre.setString(9, ustate);
        updPre.setString(10, uaddress);
        updPre.setInt(11, uid);

        rows = updPre.executeUpdate();
        System.out.println(rows + " Student(s) record Updated");
    } catch (SQLException e) {
        System.out.println("Update Error: " + e.getMessage());
    }
    return rows;
}

public String searchStu(int seid) {
    String sql = "SELECT * FROM student_detail WHERE id = ?";
    try (PreparedStatement searchstmt = conn.prepareStatement(sql)) {
        searchstmt.setInt(1, seid);
        try (ResultSet rs = searchstmt.executeQuery()) {
            if (rs.next()) {
                return "<b>ID: </b>  " + rs.getInt("id") +
                       "<br><b>Name: </b>  " + rs.getString("fname") + " " + rs.getString("lname") +
                       "<br><b> DOB: </b>  " + rs.getString("dob") +
                       "<br><b> Email: </b>  " + rs.getString("email") +
                       "<br><b> Mobile: </b>  " + rs.getString("mobNo") +
                       "<br><b> Branch: </b>  " + rs.getString("branch") +
                       "<br><b> Gender: </b>  " + rs.getString("gender") +
                       "<br><b> City: </b>  " + rs.getString("city") +
                       "<br><b> State: </b>  " + rs.getString("state") +
                       "<br><b> Address: </b>  " + rs.getString("address");
            } else {
                return "Student with ID " + seid + " does not exist.";
            }
        }
    } catch (SQLException e) {
        return "Error: " + e.getMessage();
    }
}
public void viewStu(HttpServletResponse response) throws IOException {
    String sql = "SELECT * FROM student_detail";

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        out.println("<html><body><h2>All Students</h2>");

        while (rs.next()) {
            out.println("<p>ID: " + rs.getInt("id") + "</p>");
            out.println("<p>First Name: " + rs.getString("fname") + "</p>");
            out.println("<p>Last Name: " + rs.getString("lname") + "</p>");
            out.println("<p>Date of Birth: " + rs.getString("dob") + "</p>");
            out.println("<p>Email: " + rs.getString("email") + "</p>");
            out.println("<p>Mobile: " + rs.getString("mobNo") + "</p>");
            out.println("<p>Branch: " + rs.getString("branch") + "</p>");
            out.println("<p>Gender: " + rs.getString("gender") + "</p>");
            out.println("<p>City: " + rs.getString("city") + "</p>");
            out.println("<p>State: " + rs.getString("state") + "</p>");
            out.println("<p>Address: " + rs.getString("address") + "</p>");
            out.println("<hr>");
        }

        out.println("</body></html>");

    } catch (SQLException e) {
        out.println("<p style='color:red;'>Error while fetching students: " + e.getMessage() + "</p>");
    }
}


}

public class StudentServlet extends HttpServlet {
    public StudentServlet() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StudentDB db1 = new StudentDB();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get referer (file name)
        String ref = request.getHeader("referer");
        String fileName = (ref != null) ? ref.substring(ref.lastIndexOf("/") + 1) : "unknown";

        out.println("<html><body><center>");
        out.println("<h3>Request came from file: " + fileName + "</h3>");

        // Get form parameters
       


        switch (fileName) {
            case "addStu.html":
                 int id =  Integer.parseInt(request.getParameter("id"));
                 String fname = request.getParameter("fname");
                 String lname = request.getParameter("lname");
                 String dob = request.getParameter("dob");
                 String email = request.getParameter("email");
                 String mobNo = request.getParameter("mobNo"); 
                 String branch = request.getParameter("branch");
                 String gender = request.getParameter("gender");
                 String city = request.getParameter("city");
                String state = request.getParameter("state");
                String address = request.getParameter("address");
                int r = db1.addStu(id, fname, lname, dob, email, mobNo, branch, gender, city, state, address);
                if (r > 0) {
                    out.println("<p style='color:green; font-weight:bold;font-size:25px;'>" + r + " Student(s) Inserted</p>");
                } else {
                    out.println("<p style='color:red;'>Insert Failed!</p>");
                }
                break;

            case "delStu.html":    
                        int delid =  Integer.parseInt(request.getParameter("delid"));
                        int result = db1.deleteStu(delid);

                        if (result > 0) {
                                 out.println("<p style='color:green; font-weight:bold;font-size:25px;'>" + result + " Student(s) Deleted </p>");
                       } else {
                         out.println("<p style='color:red;'>Delete Operation is Failed!</p>");
                }
                break;

            case "updStu.html":
                 int uid =  Integer.parseInt(request.getParameter("uid"));
                 String ufname = request.getParameter("ufname");
                 String ulname = request.getParameter("ulname");
                 String udob = request.getParameter("udob");
                 String uemail = request.getParameter("uemail");
                 String umobNo = request.getParameter("umobNo"); 
                 String ubranch = request.getParameter("ubranch");
                 String ugender = request.getParameter("ugender");
                 String ucity = request.getParameter("ucity");
                String ustate = request.getParameter("ustate");
                String uaddress = request.getParameter("uaddress");

                int result2 = db1.updStu(uid, ufname, ulname, udob, uemail, umobNo, ubranch, ugender, ucity, ustate, uaddress);
                if (result2 > 0) {
                                 out.println("<p style='color:green; font-weight:bold;font-size:25px;'>" + result2 + " Student(s) Updated  </p>");
                       } else {
                         out.println("<p style='color:red;ont-weight:bold;font-size:25px;'>Update Operation is Failed!</p>");}
                 
                break;

            case "searchStu.html":
                    int seid = Integer.parseInt(request.getParameter("seid")); // get student id from form
                    String resultStr = db1.searchStu(seid); // call DB method
                    out.println("<h2>Search Result</h2>");
                    out.println("<p style='font-size:18px;'>" + resultStr + "</p>");

                break;

            case "index.html":
                 db1.viewStu(response);
                break;

            default:
                out.println("<p style='color:red;'>Enter Valid Request</p>");
        }

        out.println("</center></body></html>");
    }
}
