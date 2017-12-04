package auction.service;

import auction.domain.Bid;
import javax.persistence.*;
import util.DatabaseCleaner;
import auction.domain.Category;
import auction.domain.Item;
import auction.domain.User;
import java.util.Iterator;
import nl.fontys.util.Money;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemsFromSellerTest {

    final EntityManagerFactory emf = Persistence.createEntityManagerFactory("dbi341211");
    final EntityManager em = emf.createEntityManager();
    private AuctionMgr auctionMgr;
    private RegistrationMgr registrationMgr;
    private SellerMgr sellerMgr;

    public ItemsFromSellerTest() {
    }

    @Before
    public void setUp() throws Exception {
        registrationMgr = new RegistrationMgr();
        auctionMgr = new AuctionMgr();
        sellerMgr = new SellerMgr();
        new DatabaseCleaner(em).clean();
    }

    @Test
 //   @Ignore
    public void numberOfOfferdItems() {

        String email = "ifu1@nl";
        String omsch1 = "omsch_ifu1";
        String omsch2 = "omsch_ifu2";

        User user1 = registrationMgr.registerUser(email);
        assertEquals(0, user1.numberOfOfferedItems());

        Category cat = new Category("cat2");
        Item item1 = sellerMgr.offerItem(user1, cat, omsch1);

       
        // test number of items belonging to user1
        //assertEquals(0, user1.numberOfOfferedItems());
        assertEquals(1, user1.numberOfOfferedItems());
        
        /*
         *  expected: which one of te above two assertions do you expect to be true?
         *  QUESTION:
         *    Explain the result in terms of entity manager and persistance context.
         */
         
         
        assertEquals(1, item1.getSeller().numberOfOfferedItems());


        User user2 = registrationMgr.getUser(email);
        assertEquals(1, user2.numberOfOfferedItems());
        Item item2 = sellerMgr.offerItem(user2, cat, omsch2);
        assertEquals(2, user2.numberOfOfferedItems());

        User user3 = registrationMgr.getUser(email);
        assertEquals(2, user3.numberOfOfferedItems());

        User userWithItem = item2.getSeller();
        assertEquals(2, userWithItem.numberOfOfferedItems());
        //assertEquals(3, userWithItem.numberOfOfferedItems());
        /*
         *  expected: which one of te above two assertions do you expect to be true?
         *  QUESTION:
         *    Explain the result in terms of entity manager and persistance context.
         */
        
        
        assertNotSame(user3, userWithItem);
        assertEquals(user3, userWithItem);

    }

    @Test
//    @Ignore
    public void getItemsFromSeller() {
        String email = "ifu1@nl";
        String omsch1 = "omsch_ifu1";
        String omsch2 = "omsch_ifu2";

        Category cat = new Category("cat2");

        User user10 = registrationMgr.registerUser(email);
        Item item10 = sellerMgr.offerItem(user10, cat, omsch1);
        Iterator<Item> it = user10.getOfferedItems();
        // testing number of items of java object
        assertTrue(it.hasNext());
        
        // now testing number of items for same user fetched from db.
        User user11 = registrationMgr.getUser(email);
        Iterator<Item> it11 = user11.getOfferedItems();
        assertTrue(it11.hasNext());
        it11.next();
        assertFalse(it11.hasNext());

        // Explain difference in above two tests for te iterator of 'same' user

        
        
        User user20 = registrationMgr.getUser(email);
        Item item20 = sellerMgr.offerItem(user20, cat, omsch2);
        Iterator<Item> it20 = user20.getOfferedItems();
        assertTrue(it20.hasNext());
        it20.next();
        assertTrue(it20.hasNext());


        User user30 = item20.getSeller();
        Iterator<Item> it30 = user30.getOfferedItems();
        assertTrue(it30.hasNext());
        it30.next();
        assertTrue(it30.hasNext());

    }
     @Test
    public void Opdracht2() {
        //Aanmaken van een item
        Category category = new Category("cat100");
        User user = registrationMgr.registerUser("Kappa@kappa.nl");
        Item item = sellerMgr.offerItem(user, category, "cat1");

        //Kijken of het aanmaken gelukt is
        assertEquals(1, user.numberOfOfferedItems());
        assertNull(item.getHighestBid());

        //Aanmaken van de BIDS
        Bid bid = auctionMgr.newBid(item, user, new Money(10, "Euro"));
        Bid bid1 = auctionMgr.newBid(item, user, new Money(20, "Euro"));
        Bid bid2 = auctionMgr.newBid(item, user, new Money(15, "Euro"));

        //Test na de gemaakt bid, kijken of het echt bestaat
        assertNotNull(item.getHighestBid());
        assertEquals(item, bid.getItemBid());
        assertEquals(user, bid.getBuyer());
        assertEquals(bid.getAmount(), new Money(10, "Euro"));
        assertEquals(bid1.getAmount(), new Money(20, "Euro"));
        assertEquals( bid1.getBuyer(), user);
        assertEquals(bid1.getItemBid(), item);
        assertNull(bid2);
        assertEquals(item.getHighestBid().getAmount(), new Money(20, "Euro"));
    }
}
