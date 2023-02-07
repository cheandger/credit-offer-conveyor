package org.shrek.services.impl;

/*
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class RepositoriesTest {

    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CreditRepository creditRepository;

  /*  @BeforeEach
    void setUp() {
        Client client = new Client();
        client.setFirstName("Julius");
        clientRepository.saveAndFlush(client);

        Application application = new Application();
        application.setId(456789L);
        applicationRepository.saveAndFlush(application);

        Credit credit = new Credit();
        credit.setId(123789L);
        creditRepository.saveAndFlush(credit);
    }

    @Test
    public void myTest() throws Exception {

        Client customer = new Client();
        customer.setId(100L);
        customer.setFirstName("John");
        customer.setLastName("Wick");

        clientRepository.save(customer);

        List<Client> queryResult = clientRepository.findAll();

        assertFalse(queryResult.isEmpty());
        assertNotNull(queryResult.get(0));
    }
}
*/

/*
@DataJpaTest

public class RepositoriesTest {
    @Autowired
    ApplicationRepository applicationRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CreditRepository creditRepository;

    @BeforeEach
    void setUp() {
        Client client = new Client();
        client.setFirstName("Julius");
        clientRepository.saveAndFlush(client);

        Application application = new Application();
        application.setId(456789L);
        applicationRepository.saveAndFlush(application);

        Credit credit = new Credit();
        credit.setId(123789L);
        creditRepository.saveAndFlush(credit);
    }


    @Test
    void saveToRepoTest() {

        List<Credit> findCred = creditRepository.findAll();
        Integer size = findCred.size();
        Assertions.assertEquals(1, size);
    }
}
*/