DELETE FROM next_account_id;

INSERT INTO next_account_id 
   VALUES (1 +
   (SELECT MAX(CAST (account_id AS INTEGER))
    FROM account));

DELETE FROM next_customer_id;

INSERT INTO next_customer_id
   VALUES (1 +
   (SELECT MAX(CAST (customer_id AS INTEGER))
    FROM customer));

DELETE FROM next_tx_id;

INSERT INTO next_tx_id
   VALUES (1 +
   (SELECT MAX(CAST (tx_id AS INTEGER))
    FROM tx));

