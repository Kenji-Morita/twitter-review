package controllers

/**
 * @author SAW
 */
object ResponseCode {

  // TODO SAW あとで番号を割り振る

  // ===================================================================================
  //                                                                             Success
  //                                                                             =======

  val NoReason                              = (20000, "OK")

  // ===================================================================================
  //                                                                         Bad Request
  //                                                                         ===========

  val ValidationError                       = (40000, "入力した値に異常があります")

  val PasswordsNotMatch                     = (40001, "パスワードが一致しません")

  def MailIsUsed(mail: String)              = (40002, s"メールアドレス['$mail']はすでに利用されています")

  val HashValuesNotMatch                    = (40003, "確認できませんでした")

  val SignInFailed                          = (40004, "サインインに失敗しました。メールアドレスとパスワードを確認してください")

  val NotConfirmed                          = (40005, "サインインに失敗しました。確認メールが承認されていません")

  val TextIsEmpty                           = (40000, "Text is empty")

  val AccountIsEmpty                        = (40000, "Please set screenName or mail")

  val TweetFailed                           = (40000, "Tweet failed. please try again")

  val TweetIsNotYours                       = (40000, "Target tweet is not yours")

  val TweetDeleted                          = (40000, "Already deleted")

  val Followed                              = (40000, "You already follow target member")

  val UnFollowed                            = (40000, "You Don't follow target member")

  val AlreadyValued                         = (40000, "You already valued target tweet")

  // ===================================================================================
  //                                                                        UnAuthorized
  //                                                                        ============

  val NeedSignIn                            = (40100, "この操作はサインインしている必要があります")

  // ===================================================================================
  //                                                                           Not Found
  //                                                                           =========
  val MemberNotFound                        = (40400, "ユーザが見つかりませんでした")

  val TweetNotFound                         = (40401, "つぶやきが見つかりませんでした")

  val HashNotFound                          = (40402, "Hash not found")
}
