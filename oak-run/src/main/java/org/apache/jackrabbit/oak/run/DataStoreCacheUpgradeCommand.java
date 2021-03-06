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
name|run
operator|.
name|commons
operator|.
name|Command
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
name|blob
operator|.
name|DataStoreCacheUpgradeUtils
import|;
end_import

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

begin_comment
comment|/**  * Command to upgrade JR2 DataStore cache.  */
end_comment

begin_class
specifier|public
class|class
name|DataStoreCacheUpgradeCommand
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
try|try
block|{
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|homeDirOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"homeDir"
argument_list|,
literal|"Home directory of the datastore where the pending uploads is serialized"
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
name|required
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|pathOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"path"
argument_list|,
literal|"Parent directory of the datastore"
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
name|required
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|moveCacheOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"moveCache"
argument_list|,
literal|"Move DataStore download cache"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|delPendingUploadsMapFileOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"deleteMapFile"
argument_list|,
literal|"Delete pending uploads file post upgrade"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|true
argument_list|)
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
name|OptionSet
name|options
init|=
literal|null
decl_stmt|;
try|try
block|{
name|options
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
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
return|return;
block|}
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
return|return;
block|}
name|File
name|homeDir
init|=
name|options
operator|.
name|valueOf
argument_list|(
name|homeDirOption
argument_list|)
decl_stmt|;
name|File
name|path
init|=
name|options
operator|.
name|valueOf
argument_list|(
name|pathOption
argument_list|)
decl_stmt|;
name|boolean
name|moveCache
init|=
name|options
operator|.
name|valueOf
argument_list|(
name|moveCacheOption
argument_list|)
decl_stmt|;
name|boolean
name|delPendingUploadsMapFile
init|=
name|options
operator|.
name|valueOf
argument_list|(
name|delPendingUploadsMapFileOption
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"homeDir "
operator|+
name|homeDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"path "
operator|+
name|path
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"moveCache "
operator|+
name|moveCache
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"delPendingUploadsMapFile "
operator|+
name|delPendingUploadsMapFile
argument_list|)
expr_stmt|;
name|DataStoreCacheUpgradeUtils
operator|.
name|upgrade
argument_list|(
name|homeDir
argument_list|,
name|path
argument_list|,
name|moveCache
argument_list|,
name|delPendingUploadsMapFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error upgrading cache"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

