begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|///*
end_comment

begin_comment
comment|// * Licensed to the Apache Software Foundation (ASF) under one or more
end_comment

begin_comment
comment|// * contributor license agreements.  See the NOTICE file distributed with
end_comment

begin_comment
comment|// * this work for additional information regarding copyright ownership.
end_comment

begin_comment
comment|// * The ASF licenses this file to You under the Apache License, Version 2.0
end_comment

begin_comment
comment|// * (the "License"); you may not use this file except in compliance with
end_comment

begin_comment
comment|// * the License.  You may obtain a copy of the License at
end_comment

begin_comment
comment|// *
end_comment

begin_comment
comment|// *      http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|// *
end_comment

begin_comment
comment|// * Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// * distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// * See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// * limitations under the License.
end_comment

begin_comment
comment|// */
end_comment

begin_comment
comment|//package org.apache.jackrabbit.oak.plugins.index.lucene;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import static junit.framework.Assert.assertEquals;
end_comment

begin_comment
comment|//import static org.junit.Assert.assertFalse;
end_comment

begin_comment
comment|//import static org.junit.Assert.assertTrue;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import java.util.Iterator;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.Oak;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.api.ContentRepository;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.api.Tree;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.plugins.index.IndexHookManager;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.plugins.nodetype.InitialContent;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.query.AbstractQueryTest;
end_comment

begin_comment
comment|//import org.apache.jackrabbit.oak.query.JsopUtil;
end_comment

begin_comment
comment|//import org.junit.Ignore;
end_comment

begin_comment
comment|//import org.junit.Test;
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|///**
end_comment

begin_comment
comment|// * Tests the query engine using the default index implementation: the
end_comment

begin_comment
comment|// * {@link LuceneIndexProvider}
end_comment

begin_comment
comment|// */
end_comment

begin_comment
comment|//public class LuceneIndexQueryTest extends AbstractQueryTest {
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Override
end_comment

begin_comment
comment|//    protected void createTestIndexNode() throws Exception {
end_comment

begin_comment
comment|//        Tree index = root.getTree("/");
end_comment

begin_comment
comment|//        createTestIndexNode(index, LuceneIndexConstants.TYPE_LUCENE);
end_comment

begin_comment
comment|//        root.commit();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Override
end_comment

begin_comment
comment|//    protected ContentRepository createRepository() {
end_comment

begin_comment
comment|//        return new Oak()
end_comment

begin_comment
comment|//            .with(new InitialContent())
end_comment

begin_comment
comment|//            .with(new LuceneIndexProvider())
end_comment

begin_comment
comment|//            .with(new IndexHookManager(new LuceneIndexHookProvider()))
end_comment

begin_comment
comment|//            .createContentRepository();
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test
end_comment

begin_comment
comment|//    @Ignore("OAK-420")
end_comment

begin_comment
comment|//    public void sql2() throws Exception {
end_comment

begin_comment
comment|//        test("sql2.txt");
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test
end_comment

begin_comment
comment|//    @Ignore("OAK-420")
end_comment

begin_comment
comment|//    public void sql2Measure() throws Exception {
end_comment

begin_comment
comment|//        test("sql2_measure.txt");
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test
end_comment

begin_comment
comment|//    public void descendantTest() throws Exception {
end_comment

begin_comment
comment|//        JsopUtil.apply(root, "/ + \"test\": { \"a\": {}, \"b\": {} }");
end_comment

begin_comment
comment|//        root.commit();
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        Iterator<String> result = executeQuery(
end_comment

begin_comment
comment|//                "select * from [nt:base] where isdescendantnode('/test')",
end_comment

begin_comment
comment|//                "JCR-SQL2").iterator();
end_comment

begin_comment
comment|//        assertTrue(result.hasNext());
end_comment

begin_comment
comment|//        assertEquals("/test/a", result.next());
end_comment

begin_comment
comment|//        assertEquals("/test/b", result.next());
end_comment

begin_comment
comment|//        assertFalse(result.hasNext());
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test
end_comment

begin_comment
comment|//    public void descendantTest2() throws Exception {
end_comment

begin_comment
comment|//        JsopUtil.apply(
end_comment

begin_comment
comment|//                root,
end_comment

begin_comment
comment|//                "/ + \"test\": { \"a\": { \"name\": [\"Hello\", \"World\" ] }, \"b\": { \"name\" : \"Hello\" }}");
end_comment

begin_comment
comment|//        root.commit();
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        Iterator<String> result = executeQuery(
end_comment

begin_comment
comment|//                "select * from [nt:base] where isdescendantnode('/test') and name='World'",
end_comment

begin_comment
comment|//                "JCR-SQL2").iterator();
end_comment

begin_comment
comment|//        assertTrue(result.hasNext());
end_comment

begin_comment
comment|//        assertEquals("/test/a", result.next());
end_comment

begin_comment
comment|//        assertFalse(result.hasNext());
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//    @Test
end_comment

begin_comment
comment|//    @Ignore("OAK-420")
end_comment

begin_comment
comment|//    public void ischildnodeTest() throws Exception {
end_comment

begin_comment
comment|//        JsopUtil.apply(
end_comment

begin_comment
comment|//                root,
end_comment

begin_comment
comment|//                "/ + \"parents\": { \"p0\": {\"id\": \"0\"}, \"p1\": {\"id\": \"1\"}, \"p2\": {\"id\": \"2\"}}");
end_comment

begin_comment
comment|//        JsopUtil.apply(
end_comment

begin_comment
comment|//                root,
end_comment

begin_comment
comment|//                "/ + \"children\": { \"c1\": {\"p\": \"1\"}, \"c2\": {\"p\": \"1\"}, \"c3\": {\"p\": \"2\"}, \"c4\": {\"p\": \"3\"}}");
end_comment

begin_comment
comment|//        root.commit();
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//        Iterator<String> result = executeQuery(
end_comment

begin_comment
comment|//                "select * from [nt:base] as p inner join [nt:base] as p2 on ischildnode(p2, p) where p.[jcr:path] = '/'",
end_comment

begin_comment
comment|//                "JCR-SQL2").iterator();
end_comment

begin_comment
comment|//        assertTrue(result.hasNext());
end_comment

begin_comment
comment|//        assertEquals("/, /children", result.next());
end_comment

begin_comment
comment|//        assertEquals("/, /jcr:system", result.next());
end_comment

begin_comment
comment|//        assertEquals("/, /oak:index", result.next());
end_comment

begin_comment
comment|//        assertEquals("/, /parents", result.next());
end_comment

begin_comment
comment|//        assertFalse(result.hasNext());
end_comment

begin_comment
comment|//    }
end_comment

begin_comment
comment|//}
end_comment

end_unit

