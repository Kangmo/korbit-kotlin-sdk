package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.db._


object Application extends Controller {  
	def index = Action {
		Ok(views.html.index("Korbit-SDK(Scala/Java) Test Page"))
	}
}