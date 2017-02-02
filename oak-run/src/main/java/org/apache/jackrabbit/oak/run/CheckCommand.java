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
name|segment
operator|.
name|FileStoreHelper
operator|.
name|isValidFileStoreOrFail
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
name|ArgumentAcceptingOptionSpec
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

begin_class
class|class
name|CheckCommand
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
name|ArgumentAcceptingOptionSpec
argument_list|<
name|String
argument_list|>
name|journal
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"journal"
argument_list|,
literal|"journal file"
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
literal|"journal.log"
argument_list|)
decl_stmt|;
name|OptionSpec
name|deep
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"deep"
argument_list|,
literal|"enable deep consistency checking. "
argument_list|)
decl_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|Long
argument_list|>
name|notify
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"notify"
argument_list|,
literal|"number of seconds between progress notifications"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|ArgumentAcceptingOptionSpec
argument_list|<
name|Long
argument_list|>
name|bin
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"bin"
argument_list|,
literal|"read the n first bytes from binary properties. -1 for all bytes."
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Long
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|0L
argument_list|)
decl_stmt|;
name|OptionSpec
name|segment
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"segment"
argument_list|,
literal|"Use oak-segment instead of oak-segment-tar"
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
name|nonOptionArguments
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"usage: check path/to/segmentstore<options>"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|err
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
name|dir
init|=
name|isValidFileStoreOrFail
argument_list|(
operator|new
name|File
argument_list|(
name|options
operator|.
name|nonOptionArguments
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|journalFileName
init|=
name|journal
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|boolean
name|fullTraversal
init|=
name|options
operator|.
name|has
argument_list|(
name|deep
argument_list|)
decl_stmt|;
name|long
name|debugLevel
init|=
name|notify
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|long
name|binLen
init|=
name|bin
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|SegmentUtils
operator|.
name|check
argument_list|(
name|dir
argument_list|,
name|journalFileName
argument_list|,
name|fullTraversal
argument_list|,
name|debugLevel
argument_list|,
name|binLen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SegmentTarUtils
operator|.
name|check
argument_list|(
name|dir
argument_list|,
name|journalFileName
argument_list|,
name|fullTraversal
argument_list|,
name|debugLevel
argument_list|,
name|binLen
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

