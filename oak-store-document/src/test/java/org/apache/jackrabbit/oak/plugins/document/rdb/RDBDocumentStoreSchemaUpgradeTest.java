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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|junit
operator|.
name|LogCustomizer
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
name|DocumentMK
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
name|UpdateOp
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
name|rdb
operator|.
name|RDBDocumentStore
operator|.
name|RDBTableMetaData
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
name|util
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RDBDocumentStoreSchemaUpgradeTest
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentStoreFixture
name|candidates
index|[]
init|=
operator|new
name|DocumentStoreFixture
index|[]
block|{
name|DocumentStoreFixture
operator|.
name|RDB_H2
block|,
name|DocumentStoreFixture
operator|.
name|RDB_DERBY
block|,
name|DocumentStoreFixture
operator|.
name|RDB_PG
block|,
name|DocumentStoreFixture
operator|.
name|RDB_DB2
block|,
name|DocumentStoreFixture
operator|.
name|RDB_MYSQL
block|,
name|DocumentStoreFixture
operator|.
name|RDB_ORACLE
block|,
name|DocumentStoreFixture
operator|.
name|RDB_MSSQL
block|}
decl_stmt|;
for|for
control|(
name|DocumentStoreFixture
name|dsf
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|dsf
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|dsf
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|DataSource
name|ds
decl_stmt|;
specifier|public
name|RDBDocumentStoreSchemaUpgradeTest
parameter_list|(
name|DocumentStoreFixture
name|dsf
parameter_list|)
block|{
name|this
operator|.
name|ds
operator|=
name|dsf
operator|.
name|getRDBDataSource
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|initDefault
parameter_list|()
block|{
name|RDBOptions
name|op
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T00"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|RDBDocumentStore
name|rdb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rdb
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta
init|=
name|rdb
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getTablePrefix
argument_list|()
operator|+
literal|"_NODES"
argument_list|,
name|meta
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|rdb
operator|!=
literal|null
condition|)
block|{
name|rdb
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|init01
parameter_list|()
block|{
name|LogCustomizer
name|logCustomizer
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|RDBDocumentStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
operator|.
name|contains
argument_list|(
literal|"to DB level 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|logCustomizer
operator|.
name|starting
argument_list|()
expr_stmt|;
name|RDBOptions
name|op
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T01"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|1
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|RDBDocumentStore
name|rdb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rdb
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta
init|=
name|rdb
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getTablePrefix
argument_list|()
operator|+
literal|"_NODES"
argument_list|,
name|meta
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected # of log entries: "
operator|+
name|logCustomizer
operator|.
name|getLogs
argument_list|()
argument_list|,
name|RDBDocumentStore
operator|.
name|getTableNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|logCustomizer
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logCustomizer
operator|.
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|rdb
operator|!=
literal|null
condition|)
block|{
name|rdb
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|init0then1
parameter_list|()
block|{
name|RDBOptions
name|op
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T0T1"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|RDBDocumentStore
name|rdb0
init|=
literal|null
decl_stmt|;
name|RDBDocumentStore
name|rdb1
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rdb0
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta0
init|=
name|rdb0
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|meta0
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
name|rdb1
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T0T1"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta1
init|=
name|rdb1
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|meta1
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateOp
name|testInsert
init|=
operator|new
name|UpdateOp
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rdb1
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|testInsert
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|rdb1
operator|!=
literal|null
condition|)
block|{
name|rdb1
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rdb0
operator|!=
literal|null
condition|)
block|{
name|rdb0
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|init01fail
parameter_list|()
block|{
name|LogCustomizer
name|logCustomizer
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|RDBDocumentStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Attempted to upgrade"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|logCustomizer
operator|.
name|starting
argument_list|()
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|ds
operator|instanceof
name|RDBDataSourceWrapper
argument_list|)
expr_stmt|;
name|RDBDataSourceWrapper
name|wds
init|=
operator|(
name|RDBDataSourceWrapper
operator|)
name|ds
decl_stmt|;
name|wds
operator|.
name|setFailAlterTableAddColumnStatements
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RDBOptions
name|op
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T01F"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|0
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|1
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|RDBDocumentStore
name|rdb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rdb
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta
init|=
name|rdb
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getTablePrefix
argument_list|()
operator|+
literal|"_NODES"
argument_list|,
name|meta
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|meta
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected # of log entries: "
operator|+
name|logCustomizer
operator|.
name|getLogs
argument_list|()
argument_list|,
name|RDBDocumentStore
operator|.
name|getTableNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|logCustomizer
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateOp
name|testInsert
init|=
operator|new
name|UpdateOp
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/foo"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rdb
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|testInsert
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|wds
operator|.
name|setFailAlterTableAddColumnStatements
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|logCustomizer
operator|.
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|rdb
operator|!=
literal|null
condition|)
block|{
name|rdb
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|init11
parameter_list|()
block|{
name|LogCustomizer
name|logCustomizer
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|RDBDocumentStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|INFO
argument_list|)
operator|.
name|contains
argument_list|(
literal|"to DB level 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|logCustomizer
operator|.
name|starting
argument_list|()
expr_stmt|;
name|RDBOptions
name|op
init|=
operator|new
name|RDBOptions
argument_list|()
operator|.
name|tablePrefix
argument_list|(
literal|"T11"
argument_list|)
operator|.
name|initialSchema
argument_list|(
literal|1
argument_list|)
operator|.
name|upgradeToSchema
argument_list|(
literal|1
argument_list|)
operator|.
name|dropTablesOnClose
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|RDBDocumentStore
name|rdb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|rdb
operator|=
operator|new
name|RDBDocumentStore
argument_list|(
name|this
operator|.
name|ds
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|RDBTableMetaData
name|meta
init|=
name|rdb
operator|.
name|getTable
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|op
operator|.
name|getTablePrefix
argument_list|()
operator|+
literal|"_NODES"
argument_list|,
name|meta
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|meta
operator|.
name|hasVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected # of log entries: "
operator|+
name|logCustomizer
operator|.
name|getLogs
argument_list|()
argument_list|,
literal|0
argument_list|,
name|logCustomizer
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logCustomizer
operator|.
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|rdb
operator|!=
literal|null
condition|)
block|{
name|rdb
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

