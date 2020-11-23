package com.kakaopay.payments.api.domain.repository;

import com.kakaopay.payments.api.util.Constants;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateManageId implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        String prefix = "P";
        int idSize = Constants.PayStatementSize.MANAGE_ID;

        Connection connection = session.connection();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(MANAGE_ID) FROM PAYMENT_INFO");
            if(rs.next()){
                int id = rs.getInt(1) + 1;
                StringBuilder sb = new StringBuilder(String.valueOf(id));
                int charsTo = (idSize - 1) - sb.length();
                while(charsTo > 0){
                    sb.insert(0, '0');
                    charsTo--;
                }
                return prefix + sb.toString();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
