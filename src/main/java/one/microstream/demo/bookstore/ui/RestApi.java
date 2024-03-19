package one.microstream.demo.bookstore.ui;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.app.ActionExecutor;
import one.microstream.demo.bookstore.app.ClearAction;
import one.microstream.demo.bookstore.app.QueryAction;
import one.microstream.demo.bookstore.jpa.dal.Repositories;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class RestApi {
    private final BookStoreDemo bookStoreDemo;
    private final Repositories repositories;
    private final ActionExecutor fakeExecutor;
    private final AtomicReference<Object> lastResult = new AtomicReference<>();

    public RestApi(
            final BookStoreDemo bookStoreDemo,
            final Repositories repositories
    ) {
        this.bookStoreDemo = bookStoreDemo;
        this.repositories = repositories;
        this.fakeExecutor = new ActionExecutor() {
            @Override
            public void start() {
            }

            @Override
            public void shutdown() {
            }

            @Override
            public void submit(QueryAction action) {
                try {
                    lastResult.set(action.logic().call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void remove(QueryAction action) {
            }

            @Override
            public void submit(ClearAction action) {
            }

            @Override
            public List<QueryAction> submittedQueries() {
                return null;
            }

            @Override
            public void clearQueue() {
            }

            @Override
            public void schedule(ClearAction action, int queryInterval) {
            }

            @Override
            public void unschedule(ClearAction action) {
            }
        };
    }

    @RequestMapping(value="/AllCustomersPaged")
    public Object AllCustomersPaged() {
        Query.AllCustomersPaged(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/BooksByPrice")
    public Object BooksByPrice() {
        Query.BooksByPrice(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/BooksByTitle")
    public Object BooksByTitle() {
        Query.BooksByTitle(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/BestSellerList")
    public Object BestSellerList() {
        Query.BestSellerList(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/EmployeeOfTheYear")
    public Object EmployeeOfTheYear() {
        Query.EmployeeOfTheYear(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/PurchasesOfForeigners")
    public Object PurchasesOfForeigners() {
        Query.PurchasesOfForeigners(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }

    @RequestMapping(value="/RevenueOfShopInYear")
    public Object RevenueOfShopInYear() {
        Query.RevenueOfShopInYear(bookStoreDemo.data(), repositories).actionSubmitter().accept(fakeExecutor, 1);
        return lastResult.get();
    }
}
