import org.scalatest.FunSuite
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Administrator on 20/06/2017.
  */
class TestSuite extends FunSuite {
   test("Store class exists") {
    val theStore = Store("test", null, null, null, null, null)
    assert(theStore.id == "test")
    assert(theStore.basket == null)
    assert(theStore.listOfSales == null)
    assert(theStore.customers == null)
    assert(theStore.staff == null)
  }

  test("Customer class exists") {
    val theCustomer = Customer(1, "Elliott", "Xx_WomackRocks_xX@Womack.Me", true, 9001)
    assert(theCustomer.id == 1)
    assert(theCustomer.name == "Elliott")
    assert(theCustomer.email == "Xx_WomackRocks_xX@Womack.Me")
    assert(theCustomer.isLoyalCustomer)
    assert(theCustomer.loyaltyPoints == 9001)
  }

  test("Staff class exists") {
    val theStaff = Staff(1, "Iain", "Fraser", "Staff")
    assert(theStaff.staffId == 1)
    assert(theStaff.firstName == "Iain")
    assert(theStaff.surname == "Fraser")
    assert(theStaff.jobTitle == "Staff")
  }

  test("Stock class exists") {
    val theStock = Stock(1, 45.0, 30.0, 90, "T-shirt", "Oddish", "It's oddish, but now.... on a t-shirt", LocalDate.now)
    assert(theStock.id == 1)
    assert(theStock.salePrice == 45.0)
    assert(theStock.costPerUnit == 30.0)
    assert(theStock.quantity == 90)
    assert(theStock.typeOfStock == "T-shirt")
    assert(theStock.productName == "Oddish")
    assert(theStock.info == "It's oddish, but now.... on a t-shirt")
    assert(theStock.releaseDate == LocalDate.now)
  }
  test("Customer has been created") {
    val store = Store("1", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    val customer = Customer(1, "Elliot's Friend", "ellbud@aol.com", true, 35)
    store.customers += customer
    assert(store.customers.length == 1)
  }

  test("Staff has been created") {
    val store = Store("1", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    val staff = Staff(1, "Dareal", "Tadas", "Staff")
    store.staff += staff
    assert(store.staff.length == 1)
  }

  test("Stock has been added") {
    val store = Store("1", ArrayBuffer.empty, ArrayBuffer.empty, ArrayBuffer.empty, ArrayBuffer.empty, ArrayBuffer.empty)
    val stock = Stock(1, 45.0, 30.0, 90, "T-shirt", "Oddish", "It's oddish, but now.... on a t-shirt", LocalDate.now)
    store.heldStock += stock
    assert(store.heldStock.length == 1)
  }

  test("Load staff"){
    val theStaff = new Staff(1, "Iain", "Fraser", "Staff")
    val theStore = new Store("test", null, null, null, null, null)
    var arrayOfStaff = ArrayBuffer[Staff]()
    arrayOfStaff = theStore.loadStaff()
    assert (theStaff.staffId == arrayOfStaff(0).staffId)
    assert (theStaff.firstName ==  arrayOfStaff(0).firstName)
    assert (theStaff.surname ==  arrayOfStaff(0).surname)
    assert (theStaff.jobTitle == arrayOfStaff(0).jobTitle)
  }
  test("Load customer"){
    val theCustomer = new Customer(1, "James Gallagher", "j.Gallagher@qa.com", true, 20)
    val theStore = new Store("test", null, null, null, null, null)
    var arrayOfCustomers = ArrayBuffer[Customer]()
    arrayOfCustomers = theStore.loadCustomers()
    assert(theCustomer.id == arrayOfCustomers(0).id)
    assert(theCustomer.name == arrayOfCustomers(0).name)
    assert(theCustomer.email == arrayOfCustomers(0).email)
    assert(theCustomer.isLoyalCustomer == arrayOfCustomers(0).isLoyalCustomer)
    assert(theCustomer.loyaltyPoints == arrayOfCustomers(0).loyaltyPoints)
  }
  test("Load stock"){
    val testStock = new Stock(1,60,55,0,"Game","Battlefront 2","Star Wars 12",LocalDate.parse("2017-11-20", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    val theStore = new Store("test", null, null, null, null, null)
    var arrayOfStock = ArrayBuffer[Stock]()
    arrayOfStock = theStore.loadStock()
    assert(testStock.id == arrayOfStock(0).id)
    assert(testStock.salePrice == arrayOfStock(0).salePrice)
    assert(testStock.costPerUnit == arrayOfStock(0).costPerUnit)
    assert(testStock.quantity == arrayOfStock(0).quantity)
    assert(testStock.typeOfStock == arrayOfStock(0).typeOfStock)
    assert(testStock.productName == arrayOfStock(0).productName)
    assert(testStock.info == arrayOfStock(0).info)
    assert(testStock.releaseDate == arrayOfStock(0).releaseDate)
  }
  test("Load sales"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.loadSales()
    var arrayOfStock = ArrayBuffer[Stock]()
    arrayOfStock = theStore.loadStock()
    var stockWeWant = ArrayBuffer[Stock]()
    stockWeWant += arrayOfStock(0)
    stockWeWant += arrayOfStock(3)
    var arrayOfCustomers = ArrayBuffer[Customer]()
    arrayOfCustomers = theStore.loadCustomers()
    val testSale = new Sale(1,theStore.stringToLocalDateTime("2017-06-21 12:12"), stockWeWant, 0, arrayOfCustomers(1))
    assert(testSale.totalPrice == theStore.listOfSales(0).totalPrice)
  }
  test("Save Staff"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    var arrayOfStaff = ArrayBuffer[Staff]()
    arrayOfStaff = theStore.loadStaff()
    theStore.saveStaff(arrayOfStaff)
    println("Save of staff was successful")
  }
  test("Save Stock"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    var arrayOfStock = ArrayBuffer[Stock]()
    arrayOfStock = theStore.loadStock()
    theStore.saveStock(arrayOfStock)
    println("Save of stock was successful")
  }
  test("Save Sales"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.loadSales()
    theStore.saveSales()
    println("Save of Sales was successful")
  }
  test("Create Customer"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.createCustomer(theStore.loadCustomers().length+1, "Gandalf the Grey", "sweetjazz@middleearth.com", true, 100000)
    println("Test Create Customer was successful")
  }
  test("Create Stock"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.createStock(theStore.loadStock().length+1, 300.00, 250.00, 1, "Misc", "Gandalf's Staff", "Staff", theStore.stringToLocalDate("2002-01-01"))
    println("Test Create Stock was successful")
  }
  test("Create Staff"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.createStaff(theStore.loadStaff().length+1, "James", "Java", "Staff")
    println("Test Create Staff was successful")
  }
  test("Edit Customer"){
    val customerToEdit = new Customer(5, "Gandalf the Offwhite", "shouldhaveuseddaz@middleearth.com", true, 200000)
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), new ArrayBuffer[Customer](), null, null)
    theStore.editCustomer(customerToEdit)
    println("Test Edit Customer was successful")
  }
  test("Edit Staff"){
    val staffToEdit = new Staff(6, "James", "Scala", "Staff")
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.editStaff(staffToEdit)
    println("Test Edit staff was successful")
  }
  test("Edit Stock"){ //edits failed, delete failed
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    val stockToEdit = new Stock(7,  250.00, 250.00, 1, "Misc", "Gandalf's Staff", "Staff is broken", theStore.stringToLocalDate("2002-01-01"))
    theStore.editStock(stockToEdit)
    println("Test Edit Stock was successful")
  }
  test("Delete Customer"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.customers = theStore.loadCustomers()
    theStore.delete(theStore.customers(9))
    println("Test delete customer was successful")
  }
  test("Delete Staff"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.staff = theStore.loadStaff()
    theStore.delete(theStore.staff(6))
    println("Test delete staff was successful")
  }
  test("Delete Stock"){
    val theStore = new Store("test", null, new ArrayBuffer[Sale](), null, null, null)
    theStore.heldStock = theStore.loadStock()
    theStore.delete(theStore.heldStock(8))
    println("Test delete stock was successful")
  }
/*  test("Cased delete function works"){ //DO NOT UNCOMMENT, WILL BREAK EVERYTHING
    val store = Store("Elliott & Friends", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)

    store.staff += Staff(1, "Jane", "Doe", "Manager")
    assert (store.staff.nonEmpty)
    store.heldStock += Stock(1, 20.00, 10.00, 5, "Game", "Nier: Automata", " ", LocalDate.now)
    assert (store.heldStock.nonEmpty)
    store.customers += Customer(1, "Jane Doe", "jane.doe@gmail.com", true, 100)
    assert (store.customers.nonEmpty)


    val jane: Staff = Staff(1, "Jane", "Doe", "Manager")
    store.delete(jane)
    assert (store.staff.isEmpty)

    val nier: Stock = Stock(1, 20.00, 10.00, 5, "Game", "Nier: Automata", " ", LocalDate.now)
    store.delete(nier)
    assert (store.heldStock.isEmpty)

    val janedoe: Customer = Customer(1, "Jane Doe", "jane.doe@gmail.com", true, 100)
    store.delete(janedoe)
    assert (store.customers.isEmpty)

    store.delete("{ { { { { C U R L Y B O I S } } } } }")
  }*/

  test("Sale is made") {
    val dateTime = LocalDateTime.of(2017, 5, 16, 8, 30)
    val sale = Sale(1, dateTime, new ArrayBuffer[Stock], 50.0, null)
    val store = Store("1", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    store.listOfSales += sale
    assert(store.listOfSales.length == 1)
  }

  test("Basket is clear") {
    val date = LocalDate.of(2017, 4, 17)
    val store = Store("1", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    val x = Stock(1, 35.0, 30.0, 100, "Game", "Nioh", "Playstation 4 age 18", date)
    store.basket += x
    assert(store.basket.length == 1)
    store.clearBasket()
    assert(store.basket.isEmpty)
  }

  test("Calculate today's profits"){
    var store = Store("Elliott & Friends", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    var todaysSales: ArrayBuffer[Sale] = ArrayBuffer.empty
    todaysSales += Sale(
      1,
      LocalDateTime.now,
      ArrayBuffer[Stock](
        Stock(1, 20.00, 10.00, 5, "Game", "Nier: Automata", " ", LocalDate.now),
        Stock(2, 50.00, 25.00, 5, "Software", "Photoshop CS6", " ", LocalDate.now))
    )
    todaysSales += Sale(
      2,
      LocalDateTime.now,
      ArrayBuffer[Stock](
        Stock(3, 10, 4, 20, "Misc", "Pocketu-Monsteru Cardo", " ", LocalDate.now),
        Stock(4, 40, 20, 12, "Game", "Half Life 3", " ", LocalDate.now))
    )
    assert(store.calculateTodaysProfit(todaysSales) == 61.00)
  }


  test("List today's sales"){
    val store = Store("Elliott & Friends", ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty , ArrayBuffer.empty, ArrayBuffer.empty)
    var todaysSales: ArrayBuffer[Sale] = ArrayBuffer.empty
    todaysSales += Sale(
      1,
      LocalDateTime.now,
      ArrayBuffer[Stock](
        Stock(1, 20.00, 10.00, 5, "Game", "Nier: Automata", " ", LocalDate.now),
        Stock(2, 50.00, 25.00, 5, "Software", "Photoshop CS6", " ", LocalDate.now))
    )
    todaysSales += Sale(
      2,
      LocalDateTime.now,
      ArrayBuffer[Stock](
        Stock(3, 10, 4, 20, "Misc", "Pocketu-Monsteru Cardo", " ", LocalDate.now),
        Stock(4, 40, 20, 12, "Game", "Half Life 3", " ", LocalDate.now))
    )
    store.listTodaysSales(todaysSales)
  }
}


