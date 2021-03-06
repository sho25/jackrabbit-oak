begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|xpath
operator|.
name|XPathToSQL2Converter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Test filter conditions.  */
end_comment

begin_class
specifier|public
class|class
name|FilterTest
block|{
specifier|private
specifier|final
name|SQL2Parser
name|p
init|=
name|SQL2ParserTest
operator|.
name|createTestSQL2Parser
argument_list|()
decl_stmt|;
specifier|private
name|Filter
name|createFilter
parameter_list|(
name|String
name|xpath
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|sql
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
operator|.
name|convert
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|QueryImpl
name|q
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|createFilter
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|private
name|Filter
name|createFilterSQL
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|ParseException
block|{
name|QueryImpl
name|q
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|createFilter
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|functionBasedIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql2
init|=
literal|"select [jcr:path] from [nt:base] where lower([test]) = 'hello'"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where lower([test]) = 'hello', "
operator|+
literal|"path=*, property=["
operator|+
literal|"function*lower*@test=[hello], "
operator|+
literal|"test=[is not null]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] from [nt:base] where upper([test]) = 'HELLO'"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where upper([test]) = 'HELLO', "
operator|+
literal|"path=*, property=["
operator|+
literal|"function*upper*@test=[HELLO], "
operator|+
literal|"test=[is not null]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] from [nt:base] where upper(name()) = 'ACME:TEST'"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where upper(name()) = 'ACME:TEST', "
operator|+
literal|"path=*, property=["
operator|+
literal|"function*upper*@:name=[ACME:TEST]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] from [nt:base] where lower(localname())> 'test'"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where lower(localname())> 'test', "
operator|+
literal|"path=*, property=["
operator|+
literal|"function*lower*@:localname=[(test..]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] from [nt:base] where length([test])<= 10"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where length([test])<= 10, "
operator|+
literal|"path=*, property=[function*length*@test=[..10]], "
operator|+
literal|"test=[is not null]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sql2
operator|=
literal|"select [jcr:path] from [nt:base] where length([data/test])> 2"
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Filter(query=select [jcr:path] from [nt:base] "
operator|+
literal|"where length([data/test])> 2, "
operator|+
literal|"path=*, property=[data/test=[is not null], "
operator|+
literal|"function*length*@data/test=[(2..]])"
argument_list|,
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak4170
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|sql2
init|=
literal|"select * from [nt:unstructured] where CONTAINS([jcr:content/metadata/comment], 'december')"
decl_stmt|;
name|Filter
name|f
init|=
name|createFilterSQL
argument_list|(
name|sql2
argument_list|)
decl_stmt|;
name|String
name|plan
init|=
name|f
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// with the "property is not null" restriction, it would be:
comment|// assertEquals("Filter(query=select * from [nt:unstructured] " +
comment|//         "where CONTAINS([jcr:content/metadata/comment], 'december') " +
comment|//         "fullText=jcr:content/metadata/comment:\"december\", " +
comment|//         "path=*, property=[jcr:content/metadata/comment=[is not null]])", plan);
name|assertEquals
argument_list|(
literal|"Filter(query=select * from [nt:unstructured] "
operator|+
literal|"where CONTAINS([jcr:content/metadata/comment], 'december') "
operator|+
literal|"fullText=jcr:content/metadata/comment:\"december\", "
operator|+
literal|"path=*)"
argument_list|,
name|plan
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|f
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|f
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"jcr:content/metadata/comment"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|localName
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|f
init|=
name|createFilterSQL
argument_list|(
literal|"select * from [nt:base] where localname() = 'resource'"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[resource]"
argument_list|,
name|f
operator|.
name|getPropertyRestrictions
argument_list|(
literal|":localname"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|name
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|f
init|=
name|createFilter
argument_list|(
literal|"//*[fn:name() = 'nt:resource']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[resource]"
argument_list|,
name|f
operator|.
name|getPropertyRestrictions
argument_list|(
literal|":localname"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|mvp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this can refer to a multi-valued property
name|Filter
name|f
init|=
name|createFilter
argument_list|(
literal|"//*[(@prop = 'aaa' and @prop = 'bbb' and @prop = 'ccc')]"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isNull
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this can refer to a multi-valued property
name|Filter
name|f
init|=
name|createFilter
argument_list|(
literal|"//*[not(@c)]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[is null]"
argument_list|,
name|f
operator|.
name|getPropertyRestrictions
argument_list|(
literal|"c"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isNotNull
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this can refer to a multi-valued property
name|Filter
name|f
init|=
name|createFilter
argument_list|(
literal|"//*[@c]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[is not null]"
argument_list|,
name|f
operator|.
name|getPropertyRestrictions
argument_list|(
literal|"c"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-4170"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|fulltext
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|f
init|=
name|createFilterSQL
argument_list|(
literal|"select * from [nt:unstructured] where CONTAINS([jcr:content/metadata/comment], 'december')"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"jcr:content/metadata/comment"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

