package models

import com.sksamuel.elastic4s.ElasticDsl.index
import utils.ElasticsearchUtil
import utils.PasswordUtil.crypt

/**
 * @author SAW
 */
object HashModel {

  def create(member: Member): String = ElasticsearchUtil.process { client =>
    val input = member.screenName + member.password + member.memberId
    val hash = crypt(input)
    // TODO SAW
//    client.execute(index into "twitter/hash" fields (
//      "memberId" -> member.memberId,
//      "hash" -> hash
//    ))
    hash
  }
}
