package com.torneo_tenis.repository;

import com.torneo_tenis.model.Tenista1;
import com.torneo_tenis.model.Mano;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.io.InputStream;

public class TenistaRepository implements ITenistaRepository {
    private static final Logger logger = LoggerFactory.getLogger(TenistaRepository.class);
    private Connection connection;

    public TenistaRepository() {
        initDatabase();
    }

    private void initDatabase() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(input);
            
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            
            connection = DriverManager.getConnection(url, user, password);
            createTableIfNotExists();
            logger.info("Base de datos inicializada correctamente");
        } catch (Exception e) {
            logger.error("Error al inicializar la base de datos", e);
            throw new RuntimeException(e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS tenistas (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(255) NOT NULL,
                pais VARCHAR(100) NOT NULL,
                altura INT NOT NULL,
                peso INT NOT NULL,
                puntos INT NOT NULL,
                mano VARCHAR(20) NOT NULL,
                fecha_nacimiento DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.debug("Tabla tenistas creada o ya existe");
        }
    }

    @Override
    public List<Tenista1> findAll() {
        List<Tenista1> tenistas = new ArrayList<>();
        String sql = "SELECT * FROM tenistas ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tenistas.add(mapResultSetToTenista(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al obtener todos los tenistas", e);
        }
        
        return tenistas;
    }

    @Override
    public Optional<Tenista1> findById(Long id) {
        String sql = "SELECT * FROM tenistas WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToTenista(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar tenista por ID: " + id, e);
        }
        
        return Optional.empty();
    }

    @Override
    public Tenista1 save(Tenista1 tenista) {
        String sql = """
            INSERT INTO tenistas (nombre, pais, altura, peso, puntos, mano, fecha_nacimiento, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tenista.getNombre());
            stmt.setString(2, tenista.getPais());
            stmt.setInt(3, tenista.getAltura());
            stmt.setInt(4, tenista.getPeso());
            stmt.setInt(5, tenista.getPuntos());
            stmt.setString(6, tenista.getMano().name());
            stmt.setDate(7, Date.valueOf(tenista.getFecha_nacimiento()));
            stmt.setTimestamp(8, Timestamp.valueOf(tenista.getCreated_at()));
            stmt.setTimestamp(9, Timestamp.valueOf(tenista.getUpdated_at()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tenista.setId(generatedKeys.getLong(1));
                }
            }
            
            logger.debug("Tenista guardado: " + tenista.getNombre());
            return tenista;
        } catch (SQLException e) {
            logger.error("Error al guardar tenista", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Tenista1 update(Tenista1 tenista) {
        String sql = """
            UPDATE tenistas SET nombre = ?, pais = ?, altura = ?, peso = ?, puntos = ?, 
            mano = ?, fecha_nacimiento = ?, updated_at = ? WHERE id = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tenista.getNombre());
            stmt.setString(2, tenista.getPais());
            stmt.setInt(3, tenista.getAltura());
            stmt.setInt(4, tenista.getPeso());
            stmt.setInt(5, tenista.getPuntos());
            stmt.setString(6, tenista.getMano().name());
            stmt.setDate(7, Date.valueOf(tenista.getFecha_nacimiento()));
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(9, tenista.getId());
            
            stmt.executeUpdate();
            logger.debug("Tenista actualizado: " + tenista.getNombre());
            return tenista;
        } catch (SQLException e) {
            logger.error("Error al actualizar tenista", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM tenistas WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            logger.debug("Tenista eliminado con ID: " + id);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error al eliminar tenista con ID: " + id, e);
            return false;
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM tenistas";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            logger.debug("Todos los tenistas eliminados");
        } catch (SQLException e) {
            logger.error("Error al eliminar todos los tenistas", e);
        }
    }

    @Override
    public List<Tenista1> findByPais(String pais) {
        List<Tenista1> tenistas = new ArrayList<>();
        String sql = "SELECT * FROM tenistas WHERE pais = ? ORDER BY nombre";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pais);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tenistas.add(mapResultSetToTenista(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar tenistas por país: " + pais, e);
        }
        
        return tenistas;
    }

    @Override
    public List<Tenista1> findByMano(Mano mano) {
        List<Tenista1> tenistas = new ArrayList<>();
        String sql = "SELECT * FROM tenistas WHERE mano = ? ORDER BY nombre";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mano.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tenistas.add(mapResultSetToTenista(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar tenistas por mano: " + mano, e);
        }
        
        return tenistas;
    }

    @Override
    public List<Tenista1> findByPuntosGreaterThan(int puntos) {
        List<Tenista1> tenistas = new ArrayList<>();
        String sql = "SELECT * FROM tenistas WHERE puntos > ? ORDER BY puntos DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, puntos);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tenistas.add(mapResultSetToTenista(rs));
            }
        } catch (SQLException e) {
            logger.error("Error al buscar tenistas por puntos > " + puntos, e);
        }
        
        return tenistas;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM tenistas";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error al contar tenistas", e);
        }
        
        return 0;
    }

    private Tenista1 mapResultSetToTenista(ResultSet rs) throws SQLException {
        return new Tenista1(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("pais"),
            rs.getInt("altura"),
            rs.getInt("peso"),
            rs.getInt("puntos"),
            Mano.valueOf(rs.getString("mano")),
            rs.getDate("fecha_nacimiento").toLocalDate(),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Conexión a la base de datos cerrada");
            }
        } catch (SQLException e) {
            logger.error("Error al cerrar la conexión", e);
        }
    }
}