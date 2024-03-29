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
name|jcr
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
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
name|JcrConstants
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
name|commons
operator|.
name|JcrUtils
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
name|core
operator|.
name|query
operator|.
name|AbstractQueryTest
import|;
end_import

begin_class
specifier|public
class|class
name|TextExtractionQueryTest
extends|extends
name|AbstractQueryTest
block|{
specifier|public
name|void
name|testFileContains
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFileContains
argument_list|(
literal|"test.txt"
argument_list|,
literal|"text/plain"
argument_list|,
literal|"AE502DBEA2C411DEBD340AD156D89593"
argument_list|)
expr_stmt|;
name|assertFileContains
argument_list|(
literal|"test.rtf"
argument_list|,
literal|"application/rtf"
argument_list|,
literal|"quick brown fox"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNtFile
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
while|while
condition|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
condition|)
block|{
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|String
name|content
init|=
literal|"The quick brown fox jumps over the lazy dog."
decl_stmt|;
name|Node
name|file
init|=
name|JcrUtils
operator|.
name|putFile
argument_list|(
name|testRootNode
argument_list|,
name|nodeName1
argument_list|,
literal|"text/plain"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|content
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|testRootNode
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|xpath
init|=
name|testPath
operator|+
literal|"/*[jcr:contains(jcr:content, 'lazy')]"
decl_stmt|;
name|executeXPathQuery
argument_list|(
name|xpath
argument_list|,
operator|new
name|Node
index|[]
block|{
name|file
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertFileContains
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|String
modifier|...
name|statements
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|testRootNode
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
condition|)
block|{
name|testRootNode
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|testRootNode
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|resource
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|NodeType
operator|.
name|NT_RESOURCE
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
try|try
block|{
name|Binary
name|binary
init|=
name|testRootNode
operator|.
name|getSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createBinary
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|,
name|binary
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|testRootNode
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|statement
range|:
name|statements
control|)
block|{
name|assertContainsQuery
argument_list|(
name|statement
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|private
name|void
name|assertContainsQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|boolean
name|match
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|StringBuffer
name|stmt
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"/jcr:root"
argument_list|)
operator|.
name|append
argument_list|(
name|testRoot
argument_list|)
operator|.
name|append
argument_list|(
literal|"/*"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"[jcr:contains(., '"
argument_list|)
operator|.
name|append
argument_list|(
name|statement
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"')]"
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
name|stmt
operator|.
name|toString
argument_list|()
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
decl_stmt|;
name|checkResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
name|match
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|stmt
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"SELECT * FROM nt:base "
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"WHERE jcr:path LIKE '"
argument_list|)
operator|.
name|append
argument_list|(
name|testRoot
argument_list|)
operator|.
name|append
argument_list|(
literal|"/%' "
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"AND CONTAINS(., '"
argument_list|)
operator|.
name|append
argument_list|(
name|statement
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|stmt
operator|.
name|toString
argument_list|()
argument_list|,
name|Query
operator|.
name|SQL
argument_list|)
expr_stmt|;
name|checkResult
argument_list|(
name|q
operator|.
name|execute
argument_list|()
argument_list|,
name|match
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

