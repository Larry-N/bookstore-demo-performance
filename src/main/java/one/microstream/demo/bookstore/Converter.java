package one.microstream.demo.bookstore;

import one.microstream.demo.bookstore.data.*;
import one.microstream.demo.bookstore.jpa.dal.BookSales;
import one.microstream.demo.bookstore.jpa.domain.*;

import java.util.Locale;
import java.util.stream.Collectors;

public class Converter {
    public static Customer fromEntity(CustomerEntity ce) {
        return new Customer(ce.getId().intValue(), ce.getName(), fromEntity(ce.getAddress()));
    }

    private static Address fromEntity(AddressEntity ae) {
        return new Address(ae.getAddress(), ae.getAddress2(), ae.getZipCode(), fromEntity(ae.getCity()));
    }

    private static City fromEntity(CityEntity ce) {
        return new City(ce.getName(), fromEntity(ce.getState()));
    }

    private static State fromEntity(StateEntity se) {
        return new State(se.getName(), fromEntity(se.getCountry()));
    }

    private static Country fromEntity(CountryEntity ce) {
        return new Country(ce.getName(), ce.getCode());
    }

    public static Book fromEntity(BookEntity be) {
        return new Book(be.getIsbn13(), be.getTitle(), fromEntity(be.getAuthor()),
                fromEntity(be.getGenre()), fromEntity(be.getPublisher()),
                fromEntity(be.getLanguage()), be.getPurchasePrice(), be.getRetailPrice());
    }

    private static Language fromEntity(LanguageEntity le) {
        return new Language(Locale.forLanguageTag(le.getLanguageTag()));
    }

    private static Publisher fromEntity(PublisherEntity pe) {
        return new Publisher(pe.getName(), fromEntity(pe.getAddress()));
    }

    private static Genre fromEntity(GenreEntity ge) {
        return new Genre(ge.getName());
    }

    private static Author fromEntity(AuthorEntity ae) {
        return new Author(ae.getName(), fromEntity(ae.getAddress()));
    }

    public static one.microstream.demo.bookstore.data.BookSales fromEntity(BookSales bs) {
        return new one.microstream.demo.bookstore.data.BookSales(fromEntity(bs.book()), (int) bs.amount());
    }

    public static Employee fromEntity(EmployeeEntity ee) {
        return new Employee(ee.getName(), fromEntity(ee.getAddress()));
    }

    public static Purchase fromEntity(PurchaseEntity pe) {
        return new Purchase(fromEntity(pe.getShop()), fromEntity(pe.getEmployee()), fromEntity(pe.getCustomer()),
                pe.getTimestamp(), pe.getItems().stream().map(Converter::fromEntity).collect(Collectors.toList()));
    }

    private static PurchaseItem fromEntity(PurchaseItemEntity pie) {
        return new PurchaseItem(fromEntity(pie.getBook()), pie.getAmount());
    }

    private static Shop fromEntity(ShopEntity se) {
        return new Shop(se.getName(), fromEntity(se.getAddress()));
    }
}
