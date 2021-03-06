import java.time.LocalDateTime
import java.time.LocalDate
import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import java.time.format.DateTimeFormatter

/**
  * Created by Lewis on 19/06/2017.
  */
case class Store(id: String, basket: ArrayBuffer[Stock], listOfSales: ArrayBuffer[Sale], var loggedInStaff: Staff = null) {
  def login(username: String, password: String): Boolean = {
    val staff = loadStaff()
    for(i <- 0 to staff.length-1) {
      if(username == staff(i).staffId.toString && password == staff(i).surname){
        loggedInStaff = staff(i)
        return true
      }
    }
    false
  }

  def createCustomer(id: Int, name: String, email: String, isLoyalCustomer: Boolean, loyaltyPoints: Int): Unit = {
    val tempCustomers = loadCustomers()
    tempCustomers += Customer(id, name, email, isLoyalCustomer, loyaltyPoints)
    saveCustomers(tempCustomers)
  }

  def createStaff(staffId: Int, firstName: String, surname: String, jobTitle: String): Boolean = {
    if(loggedInStaff.jobTitle == "Manager") {
      val tempStaff = loadStaff()
      tempStaff += Staff(staffId, firstName, surname, jobTitle)
      saveStaff(tempStaff)
      true
    } else false
  }

  def createStock(id: Int, salePrice: Double, costPerUnit: Double, quantity: Int, typeOfStock: String,
                  productName: String, info: String, releaseDate: LocalDate): Unit = {
    val tempStock = loadStock()
    tempStock += Stock(id, salePrice, costPerUnit, quantity, typeOfStock, productName, info, releaseDate)
    saveStock(tempStock)
  }

  def search(searchTerm: String): ArrayBuffer[Stock]  = {
    var tempBuffer = new ArrayBuffer[Stock]()
    for(item <- loadStock()) item match {
      case _ if(item.productName.toLowerCase.contains(searchTerm.toLowerCase)) => tempBuffer += item
      case _ =>
    }
    tempBuffer
  }

  def editCustomer(customerToEdit: Customer): Unit = {
    val listOfCustomers: ArrayBuffer[Customer] = loadCustomers()
    val customerIndex = customerToEdit.id - 1
    if(listOfCustomers(customerIndex).id == customerToEdit.id) listOfCustomers(customerIndex) = customerToEdit
    saveCustomers(listOfCustomers)
  }

  def editStaff(staffToEdit: Staff): Boolean = {
    if(loggedInStaff.jobTitle != "Manger") return false
    else {
      val listOfStaff: ArrayBuffer[Staff] = loadStaff()
      val staffIndex = staffToEdit.staffId - 1
      if (listOfStaff(staffIndex).staffId == staffToEdit.staffId) listOfStaff(staffIndex) = staffToEdit
      saveStaff(listOfStaff)
      return true
    }
  }

  def editStock(stockToEdit: Stock): Unit = {
    val listOfStock: ArrayBuffer[Stock] = loadStock()
    val stockIndex = stockToEdit.id - 1
    if(listOfStock(stockIndex).id == stockToEdit.id) listOfStock(stockIndex) = stockToEdit
    saveStock(listOfStock)
  }

  def delete[T](toDelete: T): Boolean = {
    if(toDelete == Staff && loggedInStaff.jobTitle != "Manage") return false
    toDelete match{
      case toDelete: Stock => if(loadStock().nonEmpty){
        val tempStock = loadStock().filter(_ != toDelete)
        saveStock(tempStock)
        return true
      }
      case toDelete: Staff => if(loadStaff().nonEmpty) {
        val tempStaff = loadStaff().filter(_ != toDelete)
        saveStaff(tempStaff)
        return true
      }
      case toDelete: Customer => if(loadCustomers().nonEmpty) {
        val tempCustomers = loadCustomers().filter(_ != toDelete)
        saveCustomers(tempCustomers)
        return true
      }
      case _ => return false
      }
    false
  }

  def makeSale(id: Int, customer: Customer = null, discountPointsToUse: Int = 0): Unit = {
    listOfSales.clear()
    var customerList = loadCustomers()

    val printReceipt = (sale: Sale, discount: Double) => {
      println(s"Sale $id: " + sale.timeOfSale + "\n  Products:")
      for (i <- sale.listOfItems.indices) {
        println("    Item " + (i + 1) + ": " + sale.listOfItems(i).productName + "    =" + sale.listOfItems(i).salePrice)
      }
      println(s"Discount: $discount \nTotal: " + sale.totalPrice)
    }

    def payWithLoyalty(sale: Sale, pointsToUse: Int): Sale = {
      val discount: Double = (pointsToUse.toDouble/100)*10
      println("Discount = " + discount)
      println("Customer Points =" + sale.customer.loyaltyPoints)
      if (discount <= sale.totalPrice && pointsToUse <= sale.customer.loyaltyPoints) {
        sale.totalPrice = sale.totalPrice - discount
        sale.customer.loyaltyPoints = sale.customer.loyaltyPoints - pointsToUse
        sale
      } else {
        println("Customer either has not got enough loyalty points or you are applying more loyalty points than the cost of the purchase, sort it out, you moron")
        sale.totalPrice = 0
        sale
      }

    }

    if (customer == null) { //Checkout as guest - WORKS
      var thisSale = Sale(id, LocalDateTime.now, basket, 0)
      printReceipt(thisSale, 0)
      loadSales()
      listOfSales += thisSale
      clearBasket()
      //saveSales()
    }
    else if (customer != null && !customer.isLoyalCustomer) { //Checkout as non-loyal Customer - Fixed
      var thisSale = Sale(id, LocalDateTime.now, basket, 0, customer)
      printReceipt(thisSale, 0)
      loadSales()
      listOfSales += thisSale
      clearBasket()
      //saveSales()
    }
    else {
      var thisSale = Sale(id, LocalDateTime.now, basket, 0, customer)
      if (thisSale.totalPrice - (discountPointsToUse/10) >= 0) {
        thisSale = payWithLoyalty(thisSale, discountPointsToUse)
        thisSale.customer.loyaltyPoints += (thisSale.totalPrice / 10).toInt
        if(thisSale.totalPrice > 0){printReceipt(thisSale, 0)
          loadSales()
          listOfSales += thisSale
          val customers = loadCustomers()
          customers.foreach(customer => if(customer.email == thisSale.customer.email){customer.loyaltyPoints = thisSale.customer.loyaltyPoints})
          saveCustomers(customerList)
          clearBasket()
          //saveSales()
        } else {
          println("Transaction failed, please try again")
        }
      }
    }
  }

  def showPoints(customer: Customer): Unit = {
    if (customer.isLoyalCustomer) {
      println("Customer is loyal, and has " + customer.loyaltyPoints + " Loyalty Points, which are worth " + ((customer.loyaltyPoints / 100) * 90) + "Pounds")
    } else println(" not a loyal dude")
  }

  def clearBasket(): Unit = {
    basket.clear()
  }

  def calculateDaysProfit(date: LocalDate): Double = {
    listOfSales.clear()
    loadSales()
    var profit: Double = 0
    var dateSales = listOfSales.filter(_.timeOfSale.toLocalDate == date)
    dateSales.foreach(sale=> sale.listOfItems.foreach(stockItem => profit += (stockItem.salePrice - stockItem.costPerUnit)))
    profit
  }


  def calculateTodaysProfit(todaysSales: ArrayBuffer[Sale]): Double = {
    var profit: Double = 0
    todaysSales.foreach(sale=> sale.listOfItems.foreach(stockItem => profit += (stockItem.salePrice - stockItem.costPerUnit)))
    profit
  }

  def listTodaysSales(todaysSales: ArrayBuffer[Sale]): Double = {
    var sellings: Double = 0
    todaysSales.foreach(sale=> sale.listOfItems.foreach(stockItem => sellings += stockItem.salePrice))
    sellings
  }

  var previousDaysSales = (yesterdaysSales: ArrayBuffer[Sale]) => {
    yesterdaysSales.map(sale=> {println(s"Sale " + sale.id); sale.listOfItems.foreach(stockItem => println("  " + stockItem.productName + "    = " + stockItem.salePrice)); println("Total = " + sale.totalPrice)})
  }

  def forecastExpectedProfit(): Unit = {

  }

  def loadCustomers(): ArrayBuffer[Customer] = {
    val currentDirectory = new java.io.File(".").getCanonicalPath  + "\\customer.txt"
    val arrayOfCustomers = ArrayBuffer[Customer]()
    for (line <- Source.fromFile(currentDirectory).getLines()) {
      val customersToReturn: Array[String] = line.split(",")
      val customerToAdd = Customer(customersToReturn(0).toInt, customersToReturn(1), customersToReturn(2), customersToReturn(3).toBoolean, customersToReturn(4).toInt)
      arrayOfCustomers += customerToAdd
    }
    arrayOfCustomers
  }

  def clearListOfSales(): Unit = {
  listOfSales.clear()
  }

  def loadSales(): Unit = {
    val currentDirectory = new java.io.File(".").getCanonicalPath  + "\\Sales.txt"
    clearListOfSales()
    for (line <- Source.fromFile(currentDirectory).getLines()) {
      val salesToReturn: Array[String] = line.split(",")
      val stock = salesToReturn(2).split("@")
      var arrayOfStock = ArrayBuffer[Stock]()
      for (i <- 0 to stock.length - 1) {
        val stockToAdd = stock(i).split("#")
        arrayOfStock += Stock(stockToAdd(0).toInt, stockToAdd(1).toDouble, stockToAdd(2).toDouble, stockToAdd(3).toInt, stockToAdd(4), stockToAdd(5), stockToAdd(6), stringToLocalDate(stockToAdd(7)))
      }
      val customerArray = salesToReturn(4).split("#")
      val customerToAdd = Customer(customerArray(0).toInt, customerArray(1), customerArray(2), customerArray(3).toBoolean, customerArray(4).toInt)
      val salesToAdd = Sale(salesToReturn(0).toInt, stringToLocalDateTime(salesToReturn(1)), arrayOfStock, 0, customerToAdd)
      listOfSales += salesToAdd
    }
  }

  def loadStaff():  ArrayBuffer[Staff] = {
    val currentDirectory = new java.io.File(".").getCanonicalPath  + "\\Staff.txt"
    val arrayOfStaff = ArrayBuffer[Staff]()
    for (line <- Source.fromFile(currentDirectory).getLines()) {
      val staffToReturn: Array[String] = line.split(",")
      val staffToAdd = Staff(staffToReturn(0).toInt, staffToReturn(1), staffToReturn(2), staffToReturn(3))
      arrayOfStaff += staffToAdd
    }
    arrayOfStaff
  }

  def loadStock(): ArrayBuffer[Stock] = {
    val currentDirectory = new java.io.File(".").getCanonicalPath  + "\\Stock.txt"
    val arrayOfStock = ArrayBuffer[Stock]()
    for (line <- Source.fromFile(currentDirectory).getLines()) {
      val stockToReturn: Array[String] = line.split(",")
      val stockToAdd = Stock(stockToReturn(0).toInt, stockToReturn(1).toDouble, stockToReturn(2).toDouble, stockToReturn(3).toInt, stockToReturn(4), stockToReturn(5), stockToReturn(6), stringToLocalDate(stockToReturn(7)))
      arrayOfStock += stockToAdd
    }
    arrayOfStock
  }

  def stringToLocalDateTime(toLocalDate: String): LocalDateTime = {
    val temp = LocalDateTime.parse(toLocalDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    temp
  }

  def stringToLocalDate(toLocalDate: String): LocalDate = {
    val temp = LocalDate.parse(toLocalDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    temp
  }

  def saveCustomers(customerToSave: ArrayBuffer[Customer]) = {
    val pw = new PrintWriter("customer.txt")
    for(i<-0 to customerToSave.length-1){
      pw.println(customerToSave(i).id + "," + customerToSave(i).name + "," + customerToSave(i).email + "," + customerToSave(i).isLoyalCustomer + "," + customerToSave(i).loyaltyPoints)
    }
    pw.close()
  }

  def saveStaff(staffToSave: ArrayBuffer[Staff]): Unit = {
    val pw = new PrintWriter("Staff.txt")
    for(i<-0 to staffToSave.length-1){
      pw.println(staffToSave(i).staffId + "," + staffToSave(i).firstName + "," + staffToSave(i).surname + "," + staffToSave(i).jobTitle)
    }
    pw.close()
  }

  def saveStock(stockToSave: ArrayBuffer[Stock]): Unit = {
    val pw = new PrintWriter("Stock.txt")
    for(i<-0 to stockToSave.length-1){
      pw.println(stockToSave(i).id + "," + stockToSave(i).salePrice + "," + stockToSave(i).costPerUnit + "," + stockToSave(i).quantity + "," + stockToSave(i).typeOfStock + "," + stockToSave(i).productName + "," + stockToSave(i).info + "," + stockToSave(i).releaseDate.toString)
    }
    pw.close
  }

  def saveSales(): Unit = {
    val pw = new PrintWriter("Sales.txt")
    for(i<-0 to listOfSales.length-1){
      var stockForSale = ""
      for(j<-0 to listOfSales(i).listOfItems.length - 1){
        stockForSale += listOfSales(i).listOfItems(j).id + "#" + listOfSales(i).listOfItems(j).salePrice + "#" + listOfSales(i).listOfItems(j).costPerUnit + "#" + listOfSales(i).listOfItems(j).quantity + "#" + listOfSales(i).listOfItems(j).typeOfStock + "#" + listOfSales(i).listOfItems(j).productName + "#" + listOfSales(i).listOfItems(j).info + "#" + listOfSales(i).listOfItems(j).releaseDate.toString
        if(j != listOfSales(i).listOfItems.length - 1) stockForSale += "@"
      }
      var customerToAdd = listOfSales(i).customer.id + "#" + listOfSales(i).customer.name + "#" + listOfSales(i).customer.email + "#" + listOfSales(i).customer.isLoyalCustomer + "#" + listOfSales(i).customer.loyaltyPoints
      val fixFormating = listOfSales(i).timeOfSale.toString.split("T")
      if(fixFormating(1).length == 8)fixFormating(1) = fixFormating(1).substring(0, fixFormating(1).length-3)
      val dateStringProper = fixFormating(0) + " " + fixFormating(1)
      pw.println(listOfSales(i).id + "," + dateStringProper + "," + stockForSale + "," + listOfSales(i).totalPrice + "," + customerToAdd)
    }
    pw.close
  }
}
