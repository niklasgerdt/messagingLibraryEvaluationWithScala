package momeval.data

import momeval.simulation.Event

trait AsyncEventRepo {
  this: DataBase =>

  def storer(store: String): Event => Unit
}
