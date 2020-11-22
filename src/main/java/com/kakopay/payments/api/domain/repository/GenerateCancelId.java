package com.kakopay.payments.api.domain.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GenerateCancelId implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String prefix = "C";
        int idSize = 20;

        Connection connection = session.connection();
        try{
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(CANCEL_ID) FROM CANCEL_INFO");
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
