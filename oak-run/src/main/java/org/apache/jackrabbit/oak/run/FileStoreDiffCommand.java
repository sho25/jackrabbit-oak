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
name|run
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
name|commons
operator|.
name|run
operator|.
name|Command
import|;
end_import

begin_class
class|class
name|FileStoreDiffCommand
implements|implements
name|Command
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|storeO
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"Path to segment store (required)"
argument_list|)
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|outO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"output"
argument_list|,
literal|"Output file"
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
name|defaultOutFile
argument_list|()
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|listOnlyO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"list"
argument_list|,
literal|"Lists available revisions"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|intervalO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"diff"
argument_list|,
literal|"Revision diff interval. Ex '--diff=R0..R1'. 'HEAD' can be used to reference the latest head revision, ie. '--diff=R0..HEAD'"
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
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|incrementalO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"incremental"
argument_list|,
literal|"Runs diffs between each subsequent revisions in the provided interval"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|pathO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"path"
argument_list|,
literal|"Filter diff by given path"
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
decl_stmt|;
name|OptionSpec
argument_list|<
name|?
argument_list|>
name|ignoreSNFEsO
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"ignore-snfes"
argument_list|,
literal|"Ignores SegmentNotFoundExceptions and continues running the diff (experimental)"
argument_list|)
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|File
name|store
init|=
name|storeO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|File
name|out
init|=
name|outO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|boolean
name|listOnly
init|=
name|options
operator|.
name|has
argument_list|(
name|listOnlyO
argument_list|)
decl_stmt|;
name|String
name|interval
init|=
name|intervalO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|boolean
name|incremental
init|=
name|options
operator|.
name|has
argument_list|(
name|incrementalO
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|pathO
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|boolean
name|ignoreSNFEs
init|=
name|options
operator|.
name|has
argument_list|(
name|ignoreSNFEsO
argument_list|)
decl_stmt|;
name|SegmentTarUtils
operator|.
name|diff
argument_list|(
name|store
argument_list|,
name|out
argument_list|,
name|listOnly
argument_list|,
name|interval
argument_list|,
name|incremental
argument_list|,
name|path
argument_list|,
name|ignoreSNFEs
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|defaultOutFile
parameter_list|()
block|{
return|return
operator|new
name|File
argument_list|(
literal|"diff_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|".log"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

