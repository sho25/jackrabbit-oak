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
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|io
operator|.
name|Closer
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
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
name|lifecycle
operator|.
name|CompositeInitializer
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
name|lifecycle
operator|.
name|RepositoryInitializer
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

begin_class
specifier|public
class|class
name|OakUpgrade
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|OptionSet
name|options
init|=
name|OptionParserFactory
operator|.
name|create
argument_list|()
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
try|try
block|{
name|MigrationCliArguments
name|cliArguments
init|=
operator|new
name|MigrationCliArguments
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|cliArguments
operator|.
name|hasOption
argument_list|(
name|OptionParserFactory
operator|.
name|HELP
argument_list|)
operator|||
name|cliArguments
operator|.
name|getArguments
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|CliUtils
operator|.
name|displayUsage
argument_list|()
expr_stmt|;
return|return;
block|}
name|migrate
argument_list|(
name|cliArguments
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
name|e
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|e
operator|.
name|getExitCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|migrate
parameter_list|(
name|MigrationCliArguments
name|argumentParser
parameter_list|)
throws|throws
name|IOException
throws|,
name|CliArgumentException
block|{
name|MigrationOptions
name|options
init|=
operator|new
name|MigrationOptions
argument_list|(
name|argumentParser
argument_list|)
decl_stmt|;
name|options
operator|.
name|logOptions
argument_list|()
expr_stmt|;
name|StoreArguments
name|stores
init|=
operator|new
name|StoreArguments
argument_list|(
name|options
argument_list|,
name|argumentParser
operator|.
name|getArguments
argument_list|()
argument_list|)
decl_stmt|;
name|stores
operator|.
name|logOptions
argument_list|()
expr_stmt|;
name|boolean
name|srcEmbedded
init|=
name|stores
operator|.
name|srcUsesEmbeddedDatastore
argument_list|()
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
name|srcEmbedded
argument_list|)
decl_stmt|;
name|migrate
argument_list|(
name|options
argument_list|,
name|stores
argument_list|,
name|datastores
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|migrate
parameter_list|(
name|MigrationOptions
name|options
parameter_list|,
name|StoreArguments
name|stores
parameter_list|,
name|DatastoreArguments
name|datastores
parameter_list|)
throws|throws
name|IOException
throws|,
name|CliArgumentException
block|{
name|Closer
name|closer
init|=
name|Closer
operator|.
name|create
argument_list|()
decl_stmt|;
name|CliUtils
operator|.
name|handleSigInt
argument_list|(
name|closer
argument_list|)
expr_stmt|;
name|MigrationFactory
name|factory
init|=
operator|new
name|MigrationFactory
argument_list|(
name|options
argument_list|,
name|stores
argument_list|,
name|datastores
argument_list|,
name|closer
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|stores
operator|.
name|getSrcStore
argument_list|()
operator|.
name|isJcr2
argument_list|()
condition|)
block|{
name|upgrade
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sidegrade
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
name|closer
operator|.
name|rethrow
argument_list|(
name|t
argument_list|)
throw|;
block|}
finally|finally
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|upgrade
parameter_list|(
name|MigrationFactory
name|migrationFactory
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|CliArgumentException
block|{
name|migrationFactory
operator|.
name|createUpgrade
argument_list|()
operator|.
name|copy
argument_list|(
name|createCompositeInitializer
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|sidegrade
parameter_list|(
name|MigrationFactory
name|migrationFactory
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
throws|,
name|CliArgumentException
block|{
name|migrationFactory
operator|.
name|createSidegrade
argument_list|()
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|RepositoryInitializer
name|createCompositeInitializer
parameter_list|()
block|{
name|ServiceLoader
argument_list|<
name|RepositoryInitializer
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|RepositoryInitializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RepositoryInitializer
argument_list|>
name|initializers
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|loader
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeInitializer
argument_list|(
name|initializers
argument_list|)
return|;
block|}
block|}
end_class

end_unit

