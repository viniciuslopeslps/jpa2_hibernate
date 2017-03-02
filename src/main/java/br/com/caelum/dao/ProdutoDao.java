package br.com.caelum.dao;

import br.com.caelum.model.Produto;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProdutoDao {

    @PersistenceContext
    private EntityManager em;

    public List<Produto> getProdutos() {
        return em.createQuery("select distinct p from Produto p join fetch p.categorias", Produto.class)
                .getResultList();
    }

    public Produto getProduto(Integer id) {
        Produto produto = em.find(Produto.class, id);
        return produto;
    }

    /**
     * Usando o Criteria para pesquisas dinamicas
     */
    @Transactional
    public List<Produto> getProdutos(String nome, Integer categoriaId, Integer lojaId) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Produto.class);

        if (!nome.isEmpty()) {
            criteria.add(Restrictions.like("nome", "%" + nome + "%"));
        }

        if (lojaId != null) {
            criteria.add(Restrictions.like("loja.id", lojaId));
        }

        if (categoriaId != null) {
            criteria.setFetchMode("categorias", FetchMode.JOIN)
                    .createAlias("categorias", "c")
                    .add(Restrictions.like("c.id", categoriaId));
        }

        return (List<Produto>) criteria.list();
    }

    public void insere(Produto produto) {
        if (produto.getId() == null)
            em.persist(produto);
        else
            em.merge(produto);
    }

}
