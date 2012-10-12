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
name|jcr
operator|.
name|query
package|;
end_package

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
name|Value
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
name|Row
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
name|ResultRow
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
name|PropertyValue
import|;
end_import

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|RowImpl
implements|implements
name|Row
block|{
specifier|private
specifier|final
name|QueryResultImpl
name|result
decl_stmt|;
specifier|private
specifier|final
name|ResultRow
name|row
decl_stmt|;
specifier|public
name|RowImpl
parameter_list|(
name|QueryResultImpl
name|result
parameter_list|,
name|ResultRow
name|row
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|row
operator|=
name|row
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|result
operator|.
name|getNode
argument_list|(
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getNode
parameter_list|(
name|String
name|selectorName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|result
operator|.
name|getNode
argument_list|(
name|getPath
argument_list|(
name|selectorName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|result
operator|.
name|getLocalPath
argument_list|(
name|row
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|(
name|String
name|selectorName
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|result
operator|.
name|getLocalPath
argument_list|(
name|row
operator|.
name|getPath
argument_list|(
name|selectorName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|double
name|getScore
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// TODO row score
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getScore
parameter_list|(
name|String
name|selectorName
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// TODO row score
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|result
operator|.
name|createValue
argument_list|(
name|row
operator|.
name|getValue
argument_list|(
name|columnName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getValues
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyValue
index|[]
name|values
init|=
name|row
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|values
operator|.
name|length
decl_stmt|;
name|Value
index|[]
name|v2
init|=
operator|new
name|Value
index|[
name|values
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|v2
index|[
name|i
index|]
operator|=
name|result
operator|.
name|createValue
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|v2
return|;
block|}
block|}
end_class

end_unit

