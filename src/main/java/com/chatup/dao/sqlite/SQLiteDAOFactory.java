package com.chatup.dao.sqlite;

import com.chatup.dao.DAOFactory;
import com.chatup.dao.MensajeDAO;
import com.chatup.dao.UsuarioDAO;

public class SQLiteDAOFactory extends DAOFactory {

    @Override
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOSQLite();
    }

    @Override
    public MensajeDAO getMensajeDAO() {
        return new MensajeDAOSQLite();
    }
}
