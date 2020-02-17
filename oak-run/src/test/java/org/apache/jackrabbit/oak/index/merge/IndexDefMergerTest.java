begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|merge
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|api
operator|.
name|CommitFailedException
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
name|commons
operator|.
name|json
operator|.
name|JsonObject
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
name|commons
operator|.
name|json
operator|.
name|JsopTokenizer
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
comment|/**  * Test merging index definitions.  */
end_comment

begin_class
specifier|public
class|class
name|IndexDefMergerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|merge
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|String
name|s
init|=
name|readFromResource
argument_list|(
literal|"merge.txt"
argument_list|)
decl_stmt|;
name|JsonObject
name|json
init|=
name|JsonObject
operator|.
name|fromJson
argument_list|(
name|s
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|JsonObject
name|e
range|:
name|array
argument_list|(
name|json
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"tests"
argument_list|)
argument_list|)
control|)
block|{
name|merge
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|mergeIndexes
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|String
name|s
init|=
name|readFromResource
argument_list|(
literal|"mergeIndexes.txt"
argument_list|)
decl_stmt|;
name|JsonObject
name|json
init|=
name|JsonObject
operator|.
name|fromJson
argument_list|(
name|s
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|JsonObject
name|e
range|:
name|array
argument_list|(
name|json
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"tests"
argument_list|)
argument_list|)
control|)
block|{
name|mergeIndexes
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|mergeIndexes
parameter_list|(
name|JsonObject
name|e
parameter_list|)
block|{
name|JsonObject
name|all
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"all"
argument_list|)
decl_stmt|;
name|JsonObject
name|newDefs
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|JsonObject
name|expectedNew
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"expectedNew"
argument_list|)
decl_stmt|;
name|IndexDefMerger
operator|.
name|merge
argument_list|(
name|newDefs
argument_list|,
name|all
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedNew
operator|.
name|toString
argument_list|()
argument_list|,
name|newDefs
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|merge
parameter_list|(
name|JsonObject
name|e
parameter_list|)
block|{
name|JsonObject
name|ancestor
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"ancestor"
argument_list|)
decl_stmt|;
name|JsonObject
name|custom
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"custom"
argument_list|)
decl_stmt|;
name|JsonObject
name|product
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"product"
argument_list|)
decl_stmt|;
try|try
block|{
name|JsonObject
name|got
init|=
name|IndexDefMerger
operator|.
name|merge
argument_list|(
name|ancestor
argument_list|,
name|custom
argument_list|,
name|product
argument_list|)
decl_stmt|;
name|JsonObject
name|expected
init|=
name|e
operator|.
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|"expected"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|,
name|got
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e2
parameter_list|)
block|{
name|String
name|expected
init|=
name|e
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"expected"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|expected
argument_list|,
literal|"\""
operator|+
name|e2
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|String
name|readFromResource
parameter_list|(
name|String
name|resourceName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|IndexDefMergerTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|resourceName
argument_list|)
argument_list|)
init|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
name|LineNumberReader
name|l
init|=
operator|new
name|LineNumberReader
argument_list|(
name|reader
argument_list|)
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|s
init|=
name|l
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|s
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|)
condition|)
block|{
comment|// comment
continue|continue;
block|}
name|buff
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
name|ArrayList
argument_list|<
name|JsonObject
argument_list|>
name|array
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|JsonObject
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|JsopTokenizer
name|tokenizer
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|read
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|tokenizer
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
name|tokenizer
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|JsonObject
name|j
init|=
name|JsonObject
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

