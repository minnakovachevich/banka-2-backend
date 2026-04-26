/**
 * Definitivni protokol za komunikaciju izmedju banaka.
 *
 * Izvor istine: {@code Info o predmetu/A protocol for bank-to-bank asset exchange.htm}
 * (autori Arsen Arsenovic i Dimitrije Andzic, 2025-10-21).
 *
 * Ovaj paket sadrzi cistu protokolnu reprezentaciju (Java records) — bez
 * persistenc anotacija, bez logike, bez veze sa nasim domenskim modelom.
 * Svrha: jedinstveni format koji svaki tim koristi pri serijalizaciji ka
 * drugim bankama, tako da svi mozemo medjusobno da komuniciramo.
 *
 * Mapping protokol → fajl:
 * <pre>
 *  §2.1 Bank identification           — RoutingNumber je int u IdempotenceKey/ForeignBankId
 *  §2.2 Idempotence keys              — IdempotenceKey
 *  §2.3 Foreign object identifiers    — ForeignBankId
 *  §2.4 Timestamps                    — java.time.OffsetDateTime (ISO8601)
 *  §2.5 Monetary values               — MonetaryValue
 *  §2.6 Accounts                      — TxAccount (sealed: Person, Account, Option)
 *  §2.7 Assets                        — Asset (sealed: Monas, Stock, OptionAsset)
 *  §2.7.1 Monetary assets             — MonetaryAsset, CurrencyCode
 *  §2.7.2 Options                     — OptionDescription
 *  §2.7.3 Stock                       — StockDescription
 *  §2.8.1 Postings                    — Posting
 *  §2.8.2 Transaction objects         — Transaction
 *  §2.12 Message types                — MessageType, Message<T>
 *  §2.12.1 NEW_TX                     — Transaction (kao body), TransactionVote, NoVoteReason
 *  §2.12.2 COMMIT_TX                  — CommitTransaction
 *  §2.12.3 ROLLBACK_TX                — RollbackTransaction
 *  §3.1 Fetching public stocks        — PublicStock
 *  §3.2 Creating OTC negotiation      — OtcOffer (request), ForeignBankId (response)
 *  §3.3 Counter-offer                 — OtcOffer
 *  §3.4 Reading negotiation           — OtcNegotiation
 *  §3.7 Friendly names                — UserInformation
 * </pre>
 *
 * Domenske klase (Account, Listing, Portfolio...) NE smeju da se serializuju
 * direktno kroz HTTP — uvek se prevode u tipove iz ovog paketa pre slanja.
 */
package rs.raf.banka2_bek.interbank.protocol;
