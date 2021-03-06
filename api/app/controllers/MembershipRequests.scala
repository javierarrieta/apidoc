package controllers

import core.Role
import lib.Validation
import db.{ MembershipRequest, Organization, OrganizationDao, User, UserDao }
import play.api.mvc._
import play.api.libs.json.Json

object MembershipRequests extends Controller {

  def get(organization_guid: Option[String], organization_key: Option[String], user_guid: Option[String], role: Option[String], limit: Int = 50, offset: Int = 0) = Authenticated { request =>
    val requests = MembershipRequest.findAll(organizationGuid = organization_guid,
                                             organizationKey = organization_key,
                                             userGuid = user_guid,
                                             role = role,
                                             limit = limit,
                                             offset = offset)
    Ok(Json.toJson(requests))
  }

  def post() = Authenticated(parse.json) { request =>
    val roleKey = (request.body \ "role").as[String]
    val role = Role.fromString(roleKey).getOrElse {
      sys.error(s"Invalid role[$roleKey]")
    }
    val organizationGuid = (request.body \ "organization_guid").as[String]
    val userGuid = (request.body \ "user_guid").as[String]

    OrganizationDao.findByGuid(organizationGuid) match {

      case None => {
        BadRequest(Json.toJson(Validation.error("Organization not found")))
      }

      case Some(org: Organization) => {
        UserDao.findByGuid(userGuid) match {

          case None => {
            BadRequest(Json.toJson(Validation.error("User not found")))
          }

          case Some(user: User) => {
            val mr = MembershipRequest.upsert(request.user, org, user, role)
            Created(Json.toJson(mr))
          }
        }
      }
    }
  }

}
