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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
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
name|assertTrue
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|BatchUpdateException
import|;
end_import

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
name|PreparedStatement
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
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|plugins
operator|.
name|document
operator|.
name|AbstractDocumentStoreTest
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
name|plugins
operator|.
name|document
operator|.
name|Collection
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
name|plugins
operator|.
name|document
operator|.
name|DocumentStoreException
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
name|plugins
operator|.
name|document
operator|.
name|DocumentStoreFixture
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
comment|/**  * Tests checking certain JDBC related features.  */
end_comment

begin_class
specifier|public
class|class
name|RDBDocumentStoreJDBCTest
extends|extends
name|AbstractDocumentStoreTest
block|{
specifier|public
name|RDBDocumentStoreJDBCTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|super
argument_list|(
name|dsf
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
name|super
operator|.
name|rdbDataSource
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|batchUpdateResult
parameter_list|()
throws|throws
name|SQLException
block|{
comment|// https://issues.apache.org/jira/browse/OAK-3938
name|assumeTrue
argument_list|(
name|super
operator|.
name|dsf
operator|!=
name|DocumentStoreFixture
operator|.
name|RDB_ORACLE
argument_list|)
expr_stmt|;
name|String
name|table
init|=
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|super
operator|.
name|ds
operator|)
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|super
operator|.
name|rdbDataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|PreparedStatement
name|st
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"DELETE FROM "
operator|+
name|table
operator|+
literal|" WHERE ID in (?, ?, ?)"
argument_list|)
decl_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|1
argument_list|,
literal|"key-1"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|2
argument_list|,
literal|"key-2"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|3
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|st
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|st
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|st
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"INSERT INTO "
operator|+
name|table
operator|+
literal|" (id) VALUES (?)"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|1
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|st
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|st
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
literal|"key-3"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|batchSt
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"UPDATE "
operator|+
name|table
operator|+
literal|" SET data = '{}' WHERE id = ?"
argument_list|)
decl_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-1"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-2"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|int
index|[]
name|batchResult
init|=
name|batchSt
operator|.
name|executeBatch
argument_list|()
decl_stmt|;
name|batchSt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// System.out.println(super.dsname + " " +
comment|// Arrays.toString(batchResult));
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|batchResult
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Row was updated although not present, status: "
operator|+
name|batchResult
index|[
literal|0
index|]
argument_list|,
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Row was updated although not present, status: "
operator|+
name|batchResult
index|[
literal|1
index|]
argument_list|,
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Row should be updated correctly."
argument_list|,
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|batchFailingInsertResult
parameter_list|()
throws|throws
name|SQLException
block|{
name|String
name|table
init|=
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|super
operator|.
name|ds
operator|)
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Connection
name|con
init|=
name|super
operator|.
name|rdbDataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
comment|// remove key-1, key-2, key-3
name|PreparedStatement
name|st
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"DELETE FROM "
operator|+
name|table
operator|+
literal|" WHERE ID in (?, ?, ?)"
argument_list|)
decl_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|1
argument_list|,
literal|"key-1"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|2
argument_list|,
literal|"key-2"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|3
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|st
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|st
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
literal|"key-3"
argument_list|)
expr_stmt|;
comment|// insert key-3
name|st
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"INSERT INTO "
operator|+
name|table
operator|+
literal|" (id) VALUES (?)"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|st
argument_list|,
literal|1
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|st
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|st
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
literal|"key-1"
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
literal|"key-2"
argument_list|)
expr_stmt|;
comment|// try to insert key-1, key-2, key-3
name|PreparedStatement
name|batchSt
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"INSERT INTO "
operator|+
name|table
operator|+
literal|" (id) VALUES (?)"
argument_list|)
decl_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-1"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-2"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|setIdInStatement
argument_list|(
name|batchSt
argument_list|,
literal|1
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|batchSt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|int
index|[]
name|batchResult
init|=
literal|null
decl_stmt|;
try|try
block|{
name|batchSt
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Batch operation should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BatchUpdateException
name|e
parameter_list|)
block|{
name|batchResult
operator|=
name|e
operator|.
name|getUpdateCounts
argument_list|()
expr_stmt|;
block|}
name|batchSt
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// System.out.println(super.dsname + " " + Arrays.toString(batchResult));
name|boolean
name|partialSuccess
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|batchResult
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
if|if
condition|(
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|0
index|]
argument_list|)
operator|&&
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
name|partialSuccess
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|batchResult
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Row already exists, shouldn't be inserted."
argument_list|,
operator|!
name|isSuccess
argument_list|(
name|batchResult
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PreparedStatement
name|rst
init|=
name|con
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT id FROM "
operator|+
name|table
operator|+
literal|" WHERE id in (?, ?, ?)"
argument_list|)
decl_stmt|;
name|setIdInStatement
argument_list|(
name|rst
argument_list|,
literal|1
argument_list|,
literal|"key-1"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|rst
argument_list|,
literal|2
argument_list|,
literal|"key-2"
argument_list|)
expr_stmt|;
name|setIdInStatement
argument_list|(
name|rst
argument_list|,
literal|3
argument_list|,
literal|"key-3"
argument_list|)
expr_stmt|;
name|ResultSet
name|results
init|=
name|rst
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|results
operator|.
name|next
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|getIdFromRS
argument_list|(
name|results
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|close
argument_list|()
expr_stmt|;
name|rst
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|partialSuccess
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Some of the rows weren't inserted."
argument_list|,
name|of
argument_list|(
literal|"key-1"
argument_list|,
literal|"key-2"
argument_list|,
literal|"key-3"
argument_list|)
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Failure reported, but rows inserted."
argument_list|,
name|of
argument_list|(
literal|"key-3"
argument_list|)
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isSuccess
parameter_list|(
name|int
name|result
parameter_list|)
block|{
return|return
name|result
operator|==
literal|1
operator|||
name|result
operator|==
name|Statement
operator|.
name|SUCCESS_NO_INFO
return|;
block|}
specifier|private
name|void
name|setIdInStatement
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|int
name|idx
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|SQLException
block|{
name|boolean
name|binaryId
init|=
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|super
operator|.
name|ds
operator|)
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
operator|.
name|isIdBinary
argument_list|()
decl_stmt|;
if|if
condition|(
name|binaryId
condition|)
block|{
try|try
block|{
name|stmt
operator|.
name|setBytes
argument_list|(
name|idx
argument_list|,
name|id
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|stmt
operator|.
name|setString
argument_list|(
name|idx
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getIdFromRS
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|idx
parameter_list|)
throws|throws
name|SQLException
block|{
name|boolean
name|binaryId
init|=
operator|(
operator|(
name|RDBDocumentStore
operator|)
name|super
operator|.
name|ds
operator|)
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
operator|.
name|isIdBinary
argument_list|()
decl_stmt|;
if|if
condition|(
name|binaryId
condition|)
block|{
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|rs
operator|.
name|getBytes
argument_list|(
name|idx
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|rs
operator|.
name|getString
argument_list|(
name|idx
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

