package com.bs.modules.spider.mapper.lucene;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.bs.common.web.domain.request.PageDomain;
import com.bs.modules.spider.domain.Article;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @descriptions: lucene索引操作层
 * @author: xucl
 * @date: 2021/8/25
 * @version: 1.0
 */
@Slf4j
@Component
public class LuceneArticleDao implements ILuceneArticleDao{

    @Autowired
    private IndexWriter indexWriter;

    @Autowired
    private Analyzer analyzer;

    @Autowired
    private SearcherManager searcherManager;

    @Override
    public void createArticleIndex(List<Article> articleList) {
        try {
            List<Document> docs = new ArrayList<Document>();
            for (Article article : articleList) {
                Document doc = new Document();
                doc.add(new StringField("articleId", article.getArticleId().toString(),Field.Store.YES));
                doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
                doc.add(new TextField("tag", article.getTag(), Field.Store.YES));
                doc.add(new StoredField("mdContent",article.getMdContent()));
                doc.add(new TextField("digest",article.getDigest(), Field.Store.YES));
                doc.add(new TextField("originalType",article.getOriginalType().toString(), Field.Store.YES));
                doc.add(new StoredField("originalAuthor",article.getOriginalAuthor()));
                doc.add(new StoredField("originalDate", DateTime.of(article.getOriginalDate()).toString()));
                doc.add(new StoredField("originalUrl",article.getOriginalUrl()));
                doc.add(new StringField("originalId",article.getOriginalId(), Field.Store.YES));
                docs.add(doc);
            }
            indexWriter.addDocuments(docs);
            indexWriter.commit();
        } catch (IOException e) {
            log.error("创建Lucene索引失败",e);
        }
    }


    @Override
    public PageInfo<Article> searchProduct(Article article, PageDomain pageDomain) throws Exception {
        searcherManager.maybeRefresh();
        IndexSearcher indexSearcher = searcherManager.acquire();

        // 查询条件
        String titleKey = article.getTitle();
        String tagKey = article.getTag();
        String originalId = article.getOriginalId();
        Short originalType = article.getOriginalType();

        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        // 标题模糊
        if (StringUtils.isNotBlank(titleKey)) {
            builder.add(new QueryParser("title", analyzer).parse(titleKey), BooleanClause.Occur.MUST);
        }
        // 类型
        if (originalType != null) {
            builder.add(new TermQuery(new Term("originalType", String.valueOf(originalType))), BooleanClause.Occur.MUST);
        }
        // 标签
        if (StringUtils.isNotBlank(tagKey)) {
            builder.add(new QueryParser("tag", analyzer).parse(tagKey), BooleanClause.Occur.MUST);
        }
        // 原始id
        if (StringUtils.isNotBlank(originalId)) {
            builder.add(new TermQuery(new Term("originalId", originalId)), BooleanClause.Occur.MUST);
        }
        if (StringUtils.isBlank(titleKey)
                && Objects.isNull(originalType)
                && StringUtils.isBlank(tagKey)
                && StringUtils.isBlank(originalId)){
            builder.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST);
        }

        PageInfo pageInfo = new PageInfo();
        TopDocs topDocs = indexSearcher.search(builder.build(), pageDomain.getPage() * pageDomain.getLimit());

        pageInfo.setTotal(topDocs.totalHits);
        ScoreDoc[] hits = topDocs.scoreDocs;
        List<Article> list = new ArrayList<>();
        int start = (pageDomain.getPage() - 1) * pageDomain.getLimit();
        for (int i = start; i < hits.length; i++) {
            Document doc = indexSearcher.doc(hits[i].doc);
            Article a = new Article().setArticleId(Long.parseLong(doc.get("articleId")))
                    .setTitle(doc.get("title"))
                    .setTag(doc.get("tag"))
                    .setMdContent(doc.get("mdContent"))
                    .setDigest(doc.get("digest"))
                    .setOriginalId(doc.get("originalId"))
                    .setOriginalUrl(doc.get("originalUrl"))
                    .setOriginalAuthor(doc.get("originalAuthor"))
                    .setOriginalDate(DateUtil.parse(doc.get("originalDate")))
                    .setOriginalType(Short.parseShort(doc.get("originalType")));
            list.add(a);
        }
        pageInfo.setList(list);
        pageInfo.setSize(list.size());
        return pageInfo;
    }
}
