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
name|plugins
operator|.
name|tika
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
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
name|joptsimple
operator|.
name|OptionSpec
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
name|run
operator|.
name|cli
operator|.
name|OptionsBean
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
name|run
operator|.
name|cli
operator|.
name|OptionsBeanFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TikaCommandOptions
implements|implements
name|OptionsBean
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"tika"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|OptionsBeanFactory
name|FACTORY
init|=
name|TikaCommandOptions
operator|::
operator|new
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|pathOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|dataFileSpecOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|tikaConfigSpecOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeDirSpecOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|indexDirSpecOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|poolSizeOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|reportAction
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|generateAction
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|populateAction
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|extractAction
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|operationNames
decl_stmt|;
specifier|private
name|OptionSet
name|options
decl_stmt|;
specifier|public
name|TikaCommandOptions
parameter_list|(
name|OptionParser
name|parser
parameter_list|)
block|{
name|pathOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"path"
argument_list|,
literal|"Path in repository under which the binaries would be searched"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|dataFileSpecOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"data-file"
argument_list|,
literal|"Data file in csv format containing the binary metadata"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
operator|new
name|File
argument_list|(
literal|"oak-binary-stats.csv"
argument_list|)
argument_list|)
expr_stmt|;
name|tikaConfigSpecOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"tika-config"
argument_list|,
literal|"Tika config file path"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
expr_stmt|;
name|storeDirSpecOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"store-path"
argument_list|,
literal|"Path of directory used to store extracted text content"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
expr_stmt|;
name|indexDirSpecOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-dir"
argument_list|,
literal|"Path of directory which stores lucene index containing extracted data"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
expr_stmt|;
name|poolSizeOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"pool-size"
argument_list|,
literal|"Size of the thread pool used to perform text extraction. Defaults "
operator|+
literal|"to number of cores on the system"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|reportAction
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"report"
argument_list|,
literal|"Generates a summary report based on the csv file"
argument_list|)
expr_stmt|;
name|generateAction
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"generate"
argument_list|,
literal|"Generates the CSV file required for 'extract' and 'report' actions"
argument_list|)
expr_stmt|;
name|populateAction
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"populate"
argument_list|,
literal|"Populates extraction store based on supplied indexed data and csv file"
argument_list|)
expr_stmt|;
name|extractAction
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"extract"
argument_list|,
literal|"Performs the text extraction based on the csv file"
argument_list|)
expr_stmt|;
name|operationNames
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"report"
argument_list|,
literal|"generate"
argument_list|,
literal|"populate"
argument_list|,
literal|"extract"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|OptionSet
name|options
parameter_list|)
block|{
name|this
operator|.
name|options
operator|=
name|options
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|title
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"The tika command supports following operations. All operations connect to repository in read only mode. \n"
operator|+
literal|"Use of one of the supported actions like --report, --generate, --populate, --extract etc. "
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
literal|50
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|operationNames
parameter_list|()
block|{
return|return
name|operationNames
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|pathOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|File
name|getDataFile
parameter_list|()
block|{
return|return
name|dataFileSpecOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|File
name|getTikaConfig
parameter_list|()
block|{
return|return
name|tikaConfigSpecOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|File
name|getStoreDir
parameter_list|()
block|{
return|return
name|storeDirSpecOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|File
name|getIndexDir
parameter_list|()
block|{
return|return
name|indexDirSpecOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isPoolSizeDefined
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|poolSizeOpt
argument_list|)
return|;
block|}
specifier|public
name|int
name|getPoolSize
parameter_list|()
block|{
return|return
name|poolSizeOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|report
parameter_list|()
block|{
comment|//The non option mode is for comparability support with previous versions
return|return
name|options
operator|.
name|has
argument_list|(
name|reportAction
argument_list|)
operator|||
name|hasNonOption
argument_list|(
literal|"report"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|generate
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|generateAction
argument_list|)
operator|||
name|hasNonOption
argument_list|(
literal|"generate"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|populate
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|populateAction
argument_list|)
operator|||
name|hasNonOption
argument_list|(
literal|"populate"
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|extract
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|extractAction
argument_list|)
operator|||
name|hasNonOption
argument_list|(
literal|"extract"
argument_list|)
return|;
block|}
specifier|public
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|getDataFileSpecOpt
parameter_list|()
block|{
return|return
name|dataFileSpecOpt
return|;
block|}
specifier|public
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|getIndexDirSpecOpt
parameter_list|()
block|{
return|return
name|indexDirSpecOpt
return|;
block|}
specifier|public
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|getStoreDirSpecOpt
parameter_list|()
block|{
return|return
name|storeDirSpecOpt
return|;
block|}
specifier|private
name|boolean
name|hasNonOption
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

