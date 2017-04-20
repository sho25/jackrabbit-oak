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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|fail
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
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the XPathToSQL2Converter  */
end_comment

begin_class
specifier|public
class|class
name|XPathTest
block|{
specifier|private
specifier|final
name|NodeTypeInfoProvider
name|nodeTypes
init|=
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|INITIAL_CONTENT
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|queryOptions
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"//(element(*, nt:address))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//(element(*, nt:address) | element(*, nt:folder))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:folder] as a "
operator|+
literal|"/* xpath: // element(*, nt:folder) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//element(*, nt:address) | //element(*, nt:folder))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:address] as a "
operator|+
literal|"/* xpath: //element(*, nt:address) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:folder] as a "
operator|+
literal|"/* xpath: //element(*, nt:folder) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/content//*[@a] order by @c option(traversal fail)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"order by [c] option(traversal FAIL) "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] "
operator|+
literal|"order by @c "
operator|+
literal|"option(traversal fail) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//*[@a or @b] order by @c option(traversal warn)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"order by [c] option(traversal WARN) "
operator|+
literal|"/* xpath: //*[@a or @b] "
operator|+
literal|"order by @c "
operator|+
literal|"option(traversal warn) */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|libs)//*[@a] order by @c option(traversal ok)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] "
operator|+
literal|"order by @c option(traversal ok) */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/libs') "
operator|+
literal|"/* xpath: /jcr:root/libs//*[@a] "
operator|+
literal|"order by @c option(traversal ok) */ "
operator|+
literal|"order by [c] "
operator|+
literal|"option(traversal OK)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|chainedConditions
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"/jcr:root/x[@a][@b][@c]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and [b] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x[@a][@b][@c] */"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|union
parameter_list|()
throws|throws
name|ParseException
block|{
name|verify
argument_list|(
literal|"(//*[@a=1 or @b=1] | //*[@c=1])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] = 1 "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] = 1 "
operator|+
literal|"/* xpath: //*[@a=1 or @b=1] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [c] = 1 "
operator|+
literal|"/* xpath: //*[@c=1] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"//(a|(b|c))"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'a' "
operator|+
literal|"/* xpath: //a */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'b' "
operator|+
literal|"/* xpath: //b */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where name(a) = 'c' "
operator|+
literal|"/* xpath: //c */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//*[jcr:contains(., 'some')])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'some') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'some')] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(//*[jcr:contains(., 'x')] | //*[jcr:contains(., 'y')])"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'x') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'x')] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where contains(*, 'y') "
operator|+
literal|"/* xpath: //*[jcr:contains(., 'y')] */"
argument_list|)
expr_stmt|;
try|try
block|{
name|verify
argument_list|(
literal|"(/jcr:root/x[@a][@b][@c]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|verify
argument_list|(
literal|"(/jcr:root/x[@a] | /jcr:root/y[@b])[@c]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x[@a] [@c] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"and [c] is not null "
operator|+
literal|"and issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y[@b][@c] */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/x | /jcr:root/y)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/x | /jcr:root/y )"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/x') "
operator|+
literal|"/* xpath: /jcr:root/x */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/y') "
operator|+
literal|"/* xpath: /jcr:root/y */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/content//*[@a] | /jcr:root/lib//*[@b]) order by @c"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a]  "
operator|+
literal|"order by @c */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [b] is not null "
operator|+
literal|"and isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//*[@b] "
operator|+
literal|"order by @c */ "
operator|+
literal|"order by [c]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)/element(*, nt:base) order by @title"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content/element(*, nt:base) "
operator|+
literal|"order by @title */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib/element(*, nt:base) "
operator|+
literal|"order by @title */ "
operator|+
literal|"order by [title]"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where issamenode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"(/jcr:root/content|/jcr:root/lib)//*"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//* */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/content/(a|b|c)/thumbnails/*"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/a/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/a/thumbnails/* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/b/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/b/thumbnails/* */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where ischildnode(a, '/content/c/thumbnails') "
operator|+
literal|"/* xpath: /jcr:root/content/c/thumbnails/* */"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
literal|"/jcr:root/(content|lib)//*[@a]"
argument_list|,
literal|"select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/content') "
operator|+
literal|"/* xpath: /jcr:root/content//*[@a] */ "
operator|+
literal|"union select [jcr:path], [jcr:score], * "
operator|+
literal|"from [nt:base] as a "
operator|+
literal|"where [a] is not null "
operator|+
literal|"and isdescendantnode(a, '/lib') "
operator|+
literal|"/* xpath: /jcr:root/lib//*[@a] */"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verify
parameter_list|(
name|String
name|xpath
parameter_list|,
name|String
name|expectedSql2
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|sql2
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
name|sql2
operator|=
name|formatSQL
argument_list|(
name|sql2
argument_list|)
expr_stmt|;
name|expectedSql2
operator|=
name|formatSQL
argument_list|(
name|expectedSql2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedSql2
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
name|SQL2Parser
name|p
init|=
operator|new
name|SQL2Parser
argument_list|(
literal|null
argument_list|,
name|nodeTypes
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|parse
argument_list|(
name|sql2
argument_list|)
expr_stmt|;
block|}
specifier|static
name|String
name|formatSQL
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" from "
argument_list|,
literal|"\nfrom "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" where "
argument_list|,
literal|"\nwhere "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" and "
argument_list|,
literal|"\nand "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" union "
argument_list|,
literal|"\nunion "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" order by "
argument_list|,
literal|"\norder by "
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" option\\("
argument_list|,
literal|"\noption\\("
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sql
operator|.
name|replaceAll
argument_list|(
literal|" \\/\\* "
argument_list|,
literal|"\n/* "
argument_list|)
expr_stmt|;
return|return
name|sql
return|;
block|}
block|}
end_class

end_unit

