package com.example.awsqldbpoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.example.awsqldbpoc.domain.Transaction;
import com.example.awsqldbpoc.repository.ContextRepository;
import com.example.awsqldbpoc.service.Ledger;
import com.example.awsqldbpoc.utils.Constants;

@Component
public class Executor implements ApplicationListener<ApplicationReadyEvent> {

	private final Ledger ledger;
	private ContextRepository contextRepository;

	public Executor(Ledger ledger, ContextRepository contextRepository) {
		super();
		this.ledger = ledger;
		this.contextRepository = contextRepository;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

		createIndex();

		long currentDate = System.currentTimeMillis();

		try {
			for (int i = 1; i <= 100; i++) {
				System.out.println("Registro " + i);

				ledger.registerTransaction(new Transaction("8345678", String.valueOf(i), LocalDateTime.now(),
						"Lancamento Teste " + i, BigDecimal.valueOf(1)));
				// ledger.registerTransaction(new Transaction("6345678",
				// UUID.randomUUID().toString(), LocalDateTime.now(),
				// "Lancamento Teste 2", BigDecimal.valueOf(-15.0)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			System.out.println("Processado em: " + (System.currentTimeMillis() - currentDate) + " ms");
		}
	}

	private void createIndex() {
		try {
			final String createIndex = String.format("CREATE INDEX ON %s (%s)", Constants.BALANCE_TABLE_NAME,
					"AccountId");

			contextRepository.execute(context -> {
				context.getTransactionExecutor().execute(createIndex);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
