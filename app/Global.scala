import play.api.{Application, GlobalSettings, Play}
import utils.ElasticsearchUtil

/**
 * @author SAW
 */
object Global extends GlobalSettings {

  override def onStart(app: Application): Unit = {
    try {
      super.onStart(app)
      ElasticsearchUtil.init(Play.current)
    } catch {
      case e: Throwable => {
      }
    }
  }
}
