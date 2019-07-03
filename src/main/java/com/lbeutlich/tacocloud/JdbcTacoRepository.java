package com.lbeutlich.tacocloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

@Repository
public class JdbcTacoRepository implements TacoRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public JdbcTacoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Taco save(Taco save) {
        long tacoid = saveTacoInfo(save);
        for (String ingredient : save.getIngredients()) {
            saveIngredientsToTaco(ingredient, tacoid);
        }

        save.setId(tacoid);
        return save;
    }

    private long saveTacoInfo(Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(
                "insert into Taco ( name, createdat) values (?, ?)",
                Types.VARCHAR,
                Types.TIMESTAMP
        );

        factory.setGeneratedKeysColumnNames("id");

        PreparedStatementCreator psc = factory
                .newPreparedStatementCreator(
                    Arrays.asList(
                            taco.getName(),
                            new Timestamp(taco.getCreatedAt().getTime())
                ));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(psc, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private void saveIngredientsToTaco(String ingredient, long tacoId) {
        jdbc.update("insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
                tacoId, ingredient);
    }
}
