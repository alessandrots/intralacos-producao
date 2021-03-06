package com.lacosdaalegria.intralacos.repository.usuario;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lacosdaalegria.intralacos.model.atividade.Hospital;
import com.lacosdaalegria.intralacos.model.usuario.Voluntario;

@Repository
public interface VoluntarioRepository extends CrudRepository<Voluntario, Long> {
	
	Voluntario findByLogin(String login);
	
	Voluntario findByEmail(String email);
	
	Voluntario findByWhatsapp(String whatsapp);
	
	Voluntario findByCpf(String cpf);
	
	@Query("SELECT count(v) FROM Voluntario v WHERE v.dtCriacao <= :data and v.promovido = FALSE and v.preferencia = :hospital and v.status = 1")
	Integer findPosicaoFila(@Param("data") Date data, @Param("hospital") Hospital hospital);
	
	@Query("SELECT count(v) FROM Voluntario v WHERE v.status = 1 and v.promovido = FALSE")
	Integer findTotalNovatos();
	
	@Query("SELECT count(v) FROM Voluntario v WHERE v.status = 1 and v.promovido = TRUE")
	Integer findTotalVoluntarios();
	
	Iterable<Voluntario> findByNascimentoLikeAndStatus(String nascimento, Integer status);
	
	Iterable<Voluntario> findTop30ByPreferenciaAndStatusAndPromovidoFalseOrderByDtCriacao(Hospital preferencia, Integer status);

	Iterable<Voluntario> findByEmailOrLoginOrWhatsappOrCpf(String email, String login, String whatsapp, String cpf);
	
	@Query(value = "SELECT v.* FROM voluntario v WHERE v.status = 1 and v.promovido = 1 and "
			+ "v.id not in (SELECT DISTINCT voluntario_id from registro r where "
			+ "r.status = 1 and r.criacao >=  now() - interval 4 month)", nativeQuery = true)
	Iterable<Voluntario> findVoluntarioDesativar();
	
	@Query(value = "SELECT v.* FROM voluntario v WHERE v.status != 1 and v.promovido = 1 and "
			+ "v.id in (SELECT DISTINCT voluntario_id from registro r where "
			+ "r.status = 1 and r.criacao >=  now() - interval 4 month)", nativeQuery = true)
	Iterable<Voluntario> findVoluntarioAtivar();

	@Query(value = "SELECT s.* FROM (SELECT v.* FROM voluntario v " +
                        "WHERE v.promovido = 0 AND " +
                              "v.status = 1 AND " +
                              "v.hospital_id = :hospital " +
                    "ORDER BY v.dt_criacao LIMIT 30) s WHERE " +
                        "NOT EXISTS (SELECT ut.id FROM controle_entrada ce " +
                                        "INNER JOIN user_token ut ON ut.id = ce.user_token_id " +
                                            "WHERE ce.status_id = 3 AND " +
                                                   "ut.voluntario_id = s.id) ", nativeQuery = true)
	Iterable<Voluntario> novatosAptosConfirmacao(@Param("hospital") Long hospital);
	
}
