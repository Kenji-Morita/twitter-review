package controllers

/**
 * @author SAW
 */
object ResponseCode {

  // TODO SAW あとで番号を割り振る

  // ===================================================================================
  //                                                                             Success
  //                                                                             =======
  val NoReason                              = (20000, "No reason")

  // ===================================================================================
  //                                                                         Bad Request
  //                                                                         ===========
  val ScreenNameIsEmpty                     = (40000, "ScreenName is empty")

  val MailIsEmpty                           = (40000, "Mail is empty")

  val PasswordIsEmpty                       = (40000, "Password is empty")

  val TextIsEmpty                           = (40000, "Text is empty")

  val AccountIsEmpty                        = (40000, "Please set screenName or mail")

  val PasswordsNotMatch                     = (40000, "Passwords are not match")

  val HashValuesNotMatch                    = (40000, "Hash values are not match")

  def ScreenNameIsUsed(screenName: String)  = (40000, s"Screen name '$screenName' is already used")

  def MailIsUsed(mail: String)              = (40000, s"Mail address '$mail' is already used ")

  val SignInFailed                          = (40000, "Sign in failed. please check screenName or mail and password")

  val TweetFailed                           = (40000, "Tweet failed. please try again")

  val TweetIsNotYours                       = (40000, "Target tweet is not yours")

  val TweetDeleted                          = (40000, "Already deleted")

  val Followed                              = (40000, "You already follow target member")

  val UnFollowed                            = (40000, "You Don't follow target member")

  // ===================================================================================
  //                                                                        UnAuthorized
  //                                                                        ============
  val NeedSignIn                            = (40100, "You need authentication")

  // ===================================================================================
  //                                                                           Not Found
  //                                                                           =========
  val MemberNotFound                        = (40400, "Member not found")

  val TweetNotFound                         = (40400, "Tweet not found")

  val HashNotFound                          = (40400, "Hash not found")
}
