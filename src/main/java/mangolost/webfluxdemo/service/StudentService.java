package mangolost.webfluxdemo.service;

import mangolost.webfluxdemo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 *
 */
@Service
public class StudentService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     *
     * @param id
     * @return
     */
    public Mono<Student> getById(final int id) {
        String sql = "select * from t_student where id = " + id;
        Student student = null;
        try {
            student = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Student.class));
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }
        return Mono.justOrEmpty(student);
    }

    /**
     *
     * @return
     */
    public Mono<Collection<Student>> getAll() {
        String sql = "select * from t_student";
        return Mono.justOrEmpty(jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class)));
    }

    /**
     *
     * @param student
     * @return
     */
    public Mono<Student> create(final Student student) {
        String sql = "insert into t_student (note, `name`, `number`, age) values (?, ?, ?, ?) ";
        jdbcTemplate.update(sql, student.getNote(), student.getName(), student.getNumber(), student.getAge());
        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);//获得刚刚插入的id
        return getById(id);
    }

    /**
     *
     * @param id
     * @param student
     * @return
     */
    public Mono<Student> update(final int id, final Student student) {
        String sql = "update t_student set note = ?, `name` = ?, `number` = ?, age = ? where id = " + id;
        jdbcTemplate.update(sql, student.getNote(), student.getName(), student.getNumber(), student.getAge());
        return getById(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public Mono<Integer> delete(final int id) {
        String sql = "delete from t_student where id = " + id;
        return Mono.justOrEmpty(jdbcTemplate.update(sql));
    }
}
