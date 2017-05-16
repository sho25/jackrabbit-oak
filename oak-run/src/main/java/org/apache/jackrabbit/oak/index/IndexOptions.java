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
name|io
operator|.
name|IOException
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|IndexOptions
implements|implements
name|OptionsBean
block|{
specifier|public
specifier|static
specifier|final
name|OptionsBeanFactory
name|FACTORY
init|=
operator|new
name|OptionsBeanFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|OptionsBean
name|newInstance
parameter_list|(
name|OptionParser
name|parser
parameter_list|)
block|{
return|return
operator|new
name|IndexOptions
argument_list|(
name|parser
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|workDirOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|outputDirOpt
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|stats
decl_stmt|;
specifier|private
specifier|final
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|definitions
decl_stmt|;
specifier|private
name|OptionSet
name|options
decl_stmt|;
specifier|public
name|IndexOptions
parameter_list|(
name|OptionParser
name|parser
parameter_list|)
block|{
name|workDirOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-work-dir"
argument_list|,
literal|"Work directory used for storing temp files"
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
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|outputDirOpt
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-out-dir"
argument_list|,
literal|"Directory used for output files"
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
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-info"
argument_list|,
literal|"Collects and dumps information related to the indexes"
argument_list|)
expr_stmt|;
name|definitions
operator|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"index-definitions"
argument_list|,
literal|"Collects and dumps index definitions"
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
specifier|public
name|File
name|getWorkDir
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|workDir
init|=
name|workDirOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
return|return
name|workDir
return|;
block|}
specifier|public
name|File
name|getOutDir
parameter_list|()
block|{
return|return
name|outputDirOpt
operator|.
name|value
argument_list|(
name|options
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|dumpStats
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|stats
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|dumpDefinitions
parameter_list|()
block|{
return|return
name|options
operator|.
name|has
argument_list|(
name|definitions
argument_list|)
return|;
block|}
block|}
end_class

end_unit

