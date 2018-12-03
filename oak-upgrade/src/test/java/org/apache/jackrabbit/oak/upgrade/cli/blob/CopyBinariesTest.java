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
name|upgrade
operator|.
name|cli
operator|.
name|blob
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
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
name|state
operator|.
name|NodeStore
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
name|upgrade
operator|.
name|cli
operator|.
name|AbstractOak2OakTest
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
name|upgrade
operator|.
name|cli
operator|.
name|OakUpgrade
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|BlobStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|FileDataStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|JdbcNodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|NodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|SegmentNodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|container
operator|.
name|SegmentTarNodeStoreContainer
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|CliArgumentException
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|DatastoreArguments
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|MigrationCliArguments
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|MigrationOptions
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|OptionParserFactory
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
name|upgrade
operator|.
name|cli
operator|.
name|parser
operator|.
name|StoreArguments
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|CopyBinariesTest
extends|extends
name|AbstractOak2OakTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CopyBinariesTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|BlobStoreContainer
name|blob
init|=
operator|new
name|FileDataStoreContainer
argument_list|()
decl_stmt|;
name|BlobStoreContainer
name|blob2
init|=
operator|new
name|FileDataStoreContainer
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, no blobstores defined, segment -> segment"
block|,
operator|new
name|SegmentNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|()
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|COPY_REFERENCES
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, no blobstores defined, segment-tar -> segment-tar"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|()
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|COPY_REFERENCES
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, no blobstores defined, segment -> segment-tar"
block|,
operator|new
name|SegmentNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|()
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|COPY_REFERENCES
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, no blobstores defined, document -> segment-tar"
block|,
operator|new
name|JdbcNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|(
literal|"--src-user=sa"
argument_list|,
literal|"--src-password=pwd"
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|COPY_REFERENCES
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, no blobstores defined, segment-tar -> document"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|JdbcNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|(
literal|"--user=abc"
argument_list|,
literal|"--password=abc"
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|UNSUPPORTED
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Missing source, external destination"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|(
literal|"--datastore="
operator|+
name|blob
operator|.
name|getDescription
argument_list|()
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|UNSUPPORTED
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy embedded to embedded, no blobstores defined"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|()
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|()
block|,
name|asList
argument_list|()
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|EMBEDDED_TO_EMBEDDED
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy embedded to external, no blobstores defined"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|()
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|(
literal|"--datastore="
operator|+
name|blob
operator|.
name|getDescription
argument_list|()
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|EMBEDDED_TO_EXTERNAL
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy references, src blobstore defined"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
name|asList
argument_list|(
literal|"--src-datastore="
operator|+
name|blob
operator|.
name|getDescription
argument_list|()
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|COPY_REFERENCES
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy external to embedded, src blobstore defined"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|()
block|,
name|asList
argument_list|(
literal|"--copy-binaries"
argument_list|,
literal|"--src-datastore="
operator|+
name|blob
operator|.
name|getDescription
argument_list|()
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|EXTERNAL_TO_EMBEDDED
block|}
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"Copy external to external, src blobstore defined"
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob
argument_list|)
block|,
operator|new
name|SegmentTarNodeStoreContainer
argument_list|(
name|blob2
argument_list|)
block|,
name|asList
argument_list|(
literal|"--copy-binaries"
argument_list|,
literal|"--src-datastore="
operator|+
name|blob
operator|.
name|getDescription
argument_list|()
argument_list|,
literal|"--datastore="
operator|+
name|blob2
operator|.
name|getDescription
argument_list|()
argument_list|)
block|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|EXTERNAL_TO_EXTERNAL
block|}
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
specifier|private
specifier|final
name|NodeStoreContainer
name|source
decl_stmt|;
specifier|private
specifier|final
name|NodeStoreContainer
name|destination
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|args
decl_stmt|;
specifier|private
specifier|final
name|DatastoreArguments
operator|.
name|BlobMigrationCase
name|blobMigrationCase
decl_stmt|;
specifier|public
name|CopyBinariesTest
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeStoreContainer
name|source
parameter_list|,
name|NodeStoreContainer
name|destination
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|,
name|DatastoreArguments
operator|.
name|BlobMigrationCase
name|blobMigrationCase
parameter_list|)
throws|throws
name|IOException
throws|,
name|CliArgumentException
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|blobMigrationCase
operator|=
name|blobMigrationCase
expr_stmt|;
name|this
operator|.
name|source
operator|.
name|clean
argument_list|()
expr_stmt|;
name|this
operator|.
name|destination
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStoreContainer
name|getSourceContainer
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeStoreContainer
name|getDestinationContainer
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|getArgs
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|asList
argument_list|(
literal|"--disable-mmap"
argument_list|,
literal|"--skip-checkpoints"
argument_list|,
name|source
operator|.
name|getDescription
argument_list|()
argument_list|,
name|destination
operator|.
name|getDescription
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|source
init|=
name|getSourceContainer
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|initContent
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|getSourceContainer
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|args
init|=
name|getArgs
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"oak2oak {}"
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|MigrationCliArguments
name|cliArgs
init|=
operator|new
name|MigrationCliArguments
argument_list|(
name|OptionParserFactory
operator|.
name|create
argument_list|()
operator|.
name|parse
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|MigrationOptions
name|options
init|=
operator|new
name|MigrationOptions
argument_list|(
name|cliArgs
argument_list|)
decl_stmt|;
name|StoreArguments
name|stores
init|=
operator|new
name|StoreArguments
argument_list|(
name|options
argument_list|,
name|cliArgs
operator|.
name|getArguments
argument_list|()
argument_list|)
decl_stmt|;
name|DatastoreArguments
name|datastores
init|=
operator|new
name|DatastoreArguments
argument_list|(
name|options
argument_list|,
name|stores
argument_list|,
name|stores
operator|.
name|srcUsesEmbeddedDatastore
argument_list|()
argument_list|)
decl_stmt|;
name|OakUpgrade
operator|.
name|migrate
argument_list|(
name|options
argument_list|,
name|stores
argument_list|,
name|datastores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blobMigrationCase
argument_list|,
name|datastores
operator|.
name|getBlobMigrationCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CliArgumentException
name|e
parameter_list|)
block|{
if|if
condition|(
name|blobMigrationCase
operator|==
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|UNSUPPORTED
condition|)
block|{
return|return;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|createSession
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
specifier|public
name|void
name|validateMigration
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
throws|,
name|CliArgumentException
block|{
if|if
condition|(
name|blobMigrationCase
operator|==
name|DatastoreArguments
operator|.
name|BlobMigrationCase
operator|.
name|UNSUPPORTED
condition|)
block|{
return|return;
block|}
name|super
operator|.
name|validateMigration
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

