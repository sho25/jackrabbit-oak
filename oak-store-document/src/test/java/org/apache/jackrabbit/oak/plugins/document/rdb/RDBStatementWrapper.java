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
name|plugins
operator|.
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLWarning
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_class
specifier|public
class|class
name|RDBStatementWrapper
implements|implements
name|Statement
block|{
specifier|private
specifier|final
name|Statement
name|statement
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|failAlterTableAddColumnStatements
decl_stmt|;
specifier|public
name|RDBStatementWrapper
parameter_list|(
name|Statement
name|statement
parameter_list|,
name|boolean
name|failAlterTableAddColumnStatements
parameter_list|)
block|{
name|this
operator|.
name|statement
operator|=
name|statement
expr_stmt|;
name|this
operator|.
name|failAlterTableAddColumnStatements
operator|=
name|failAlterTableAddColumnStatements
expr_stmt|;
block|}
specifier|public
name|void
name|addBatch
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|addBatch
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|cancel
parameter_list|()
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|clearBatch
parameter_list|()
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|clearBatch
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|clearWarnings
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|closeOnCompletion
parameter_list|()
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|closeOnCompletion
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|,
name|autoGeneratedKeys
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
index|[]
name|columnIndexes
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|,
name|columnIndexes
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
index|[]
name|columnNames
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|,
name|columnNames
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|execute
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|this
operator|.
name|failAlterTableAddColumnStatements
condition|)
block|{
name|String
name|l
init|=
name|sql
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|startsWith
argument_list|(
literal|"alter table "
argument_list|)
operator|&&
name|l
operator|.
name|contains
argument_list|(
literal|" add "
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|l
operator|.
name|contains
argument_list|(
literal|" constraint "
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"table alter statement rejected: "
operator|+
name|sql
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
name|int
index|[]
name|executeBatch
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeBatch
argument_list|()
return|;
block|}
specifier|public
name|ResultSet
name|executeQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|autoGeneratedKeys
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|,
name|autoGeneratedKeys
argument_list|)
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
index|[]
name|columnIndexes
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|,
name|columnIndexes
argument_list|)
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
index|[]
name|columnNames
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|,
name|columnNames
argument_list|)
return|;
block|}
specifier|public
name|int
name|executeUpdate
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|executeUpdate
argument_list|(
name|sql
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getConnection
argument_list|()
return|;
block|}
specifier|public
name|int
name|getFetchDirection
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getFetchDirection
argument_list|()
return|;
block|}
specifier|public
name|int
name|getFetchSize
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getFetchSize
argument_list|()
return|;
block|}
specifier|public
name|ResultSet
name|getGeneratedKeys
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getGeneratedKeys
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMaxFieldSize
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getMaxFieldSize
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMaxRows
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getMaxRows
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getMoreResults
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getMoreResults
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getMoreResults
parameter_list|(
name|int
name|current
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getMoreResults
argument_list|(
name|current
argument_list|)
return|;
block|}
specifier|public
name|int
name|getQueryTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getQueryTimeout
argument_list|()
return|;
block|}
specifier|public
name|ResultSet
name|getResultSet
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getResultSet
argument_list|()
return|;
block|}
specifier|public
name|int
name|getResultSetConcurrency
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getResultSetConcurrency
argument_list|()
return|;
block|}
specifier|public
name|int
name|getResultSetHoldability
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getResultSetHoldability
argument_list|()
return|;
block|}
specifier|public
name|int
name|getResultSetType
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getResultSetType
argument_list|()
return|;
block|}
specifier|public
name|int
name|getUpdateCount
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getUpdateCount
argument_list|()
return|;
block|}
specifier|public
name|SQLWarning
name|getWarnings
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|getWarnings
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isCloseOnCompletion
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|isCloseOnCompletion
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|isClosed
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isPoolable
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|isPoolable
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|isWrapperFor
argument_list|(
name|iface
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCursorName
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setCursorName
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setEscapeProcessing
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setEscapeProcessing
argument_list|(
name|enable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFetchDirection
parameter_list|(
name|int
name|direction
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setFetchDirection
argument_list|(
name|direction
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFetchSize
parameter_list|(
name|int
name|rows
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setFetchSize
argument_list|(
name|rows
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxFieldSize
parameter_list|(
name|int
name|max
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setMaxFieldSize
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxRows
parameter_list|(
name|int
name|max
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setMaxRows
argument_list|(
name|max
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPoolable
parameter_list|(
name|boolean
name|poolable
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setPoolable
argument_list|(
name|poolable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setQueryTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SQLException
block|{
name|statement
operator|.
name|setQueryTimeout
argument_list|(
name|seconds
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|statement
operator|.
name|unwrap
argument_list|(
name|iface
argument_list|)
return|;
block|}
block|}
end_class

end_unit
