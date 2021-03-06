package OrientDBClient;

import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

/**
 * Servlet implementation class ClientServlet
 */
@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClientServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getServletPath();
		
		switch (action) {
        case "/newMovieStarForm":
        	AddNewMovieStar(request,response);
            break;
        case "/insert":
            AddNewStar(request,response);
            break;
        case "/delete":
        	deleteMovieStar(request,response);
            break;
        case "/edit":
        	EditMovieStarForm(request,response);
            break;
        case "/update":
        	EditMovieStar(request,response);
            break;
        default:
        	listMovieStars(request, response);
            break;
        }
		
	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	private void listMovieStars(HttpServletRequest request, HttpServletResponse response)
	{
		OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		 ODatabaseSession db =  orient.open("Movies","root","root12345");
		 String query = "select from MovieStar";
		 OResultSet rs = db.query(query);
		 ArrayList result = new ArrayList();
		 while(rs.hasNext())
		 {
			 OResult item = rs.next();
			 String Name = item.getProperty("Name");
			 String Address = item.getProperty("Address");
			 String Gender = item.getProperty("Gender");
			 String BirthDate = item.getProperty("BirthDate").toString();
			 String StarID = item.getProperty("StarID");
			 
			 MovieStar ms = new MovieStar(StarID, Name, Address, Gender, BirthDate);
			 result.add(ms);
		 }
		 
		 db.close();
		 orient.close();
		request.setAttribute("ListOfMovies", result);
	    RequestDispatcher dispatcher = request.getRequestDispatcher("ListOfMovies.jsp");
	    try {
			dispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	    
	}
	
	
	private void AddNewMovieStar(HttpServletRequest request,HttpServletResponse response) {
		RequestDispatcher dispatcher = request.getRequestDispatcher("AddNewStar.jsp");
        try {
			dispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void AddNewStar(HttpServletRequest request,HttpServletResponse response)
	{
		 OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		 ODatabaseSession db =  orient.open("Movies","root","root12345");
		 
		 String Name = request.getParameter("StarName");
		 String Address = request.getParameter("StarAddress");
		 String Gender = request.getParameter("StarGender");
		 String StarBD = request.getParameter("StarBD");
		 String uniqueID = UUID.randomUUID().toString();
		 
		 OVertex newStar = db.newVertex("MovieStar");
		 newStar.setProperty("Name", Name);
		 newStar.setProperty("Address", Address);
		 newStar.setProperty("Gender", Gender);
		 newStar.setProperty("BirthDate", StarBD);
		 newStar.setProperty("StarID", uniqueID);
		 newStar.save();
		 
		 try {
			response.sendRedirect("/OrientDBClient3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
				
		
		
	}
	private void EditMovieStarForm(HttpServletRequest request,HttpServletResponse response)
	{
		 String SID = (request.getParameter("StarID")).toString();
		 OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		 ODatabaseSession db =  orient.open("Movies","root","root12345");
		 String query = "SELECT FROM MovieStar WHERE StarID = "+ SID ;
		 
		 OResultSet rs = db.query(query);
		 MovieStar ms = new MovieStar();
		 while(rs.hasNext())
		 {
			 OResult item = rs.next();
			 ms.Name = item.getProperty("Name");
			 ms.Address = item.getProperty("Address");
			 ms.Gender = item.getProperty("Gender");
			 ms.BirthDate = item.getProperty("BirthDate").toString();
			 
		 }
		 db.close();
		 orient.close();
		request.setAttribute("StarToEdit", ms);
	    RequestDispatcher dispatcher = request.getRequestDispatcher("MovieStarEdit.jsp");
	    try {
			dispatcher.forward(request, response);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		
	}
	private void EditMovieStar(HttpServletRequest request,HttpServletResponse response)
	{
		 String SID = request.getParameter("StarID").toString();
		 OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		 ODatabaseSession db =  orient.open("Movies","root","root12345");
		 
		 String query = "UPDATE MovieStar SET Name = "+ request.getParameter("Name")+","
		 +"Address = "+ request.getParameter("Address")+","+"BirthDate = "
				 + request.getParameter("BirthDate") + "WHERE StarID = " + SID ;
		 
		 db.command(query);
		 
		 try {
				response.sendRedirect("/OrientDBClient3");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	}
	
	private void deleteMovieStar(HttpServletRequest request,HttpServletResponse response)
	{
		String SID = request.getParameter("StarID").toString();
		 OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		 ODatabaseSession db =  orient.open("Movies","root","root12345");
		 
		 String query = "DELETE VERTEX MovieStar WHERE StarID =  " + SID;
		 
		 db.command(query);
		 try {
				response.sendRedirect("/OrientDBClient3");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
	}
	
}
