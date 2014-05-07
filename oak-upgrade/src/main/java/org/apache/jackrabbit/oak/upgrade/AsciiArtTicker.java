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
name|upgrade
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Iterators
import|;
end_import

begin_class
specifier|public
class|class
name|AsciiArtTicker
implements|implements
name|ProgressTicker
block|{
annotation|@
name|Override
specifier|public
name|String
name|tick
parameter_list|()
block|{
return|return
name|ticks
operator|.
name|next
argument_list|()
return|;
block|}
specifier|private
specifier|final
name|String
index|[]
name|message
init|=
operator|new
name|String
index|[]
block|{
literal|" __        __"
block|,
literal|" \\ \\      / /"
block|,
literal|"  \\ \\ /\\ / / "
block|,
literal|"   \\ V  V /  "
block|,
literal|"    \\_/\\_/   "
block|,
literal|"             "
block|,
literal|"  _____ "
block|,
literal|" | ____|"
block|,
literal|" |  _|  "
block|,
literal|" | |___ "
block|,
literal|" |_____|"
block|,
literal|"        "
block|,
literal|"  _     "
block|,
literal|" | |    "
block|,
literal|" | |    "
block|,
literal|" | |___ "
block|,
literal|" |_____|"
block|,
literal|"        "
block|,
literal|"   ____ "
block|,
literal|"  / ___|"
block|,
literal|" | |    "
block|,
literal|" | |___ "
block|,
literal|"  \\____|"
block|,
literal|"        "
block|,
literal|"   ___  "
block|,
literal|"  / _ \\ "
block|,
literal|" | | | |"
block|,
literal|" | |_| |"
block|,
literal|"  \\___/ "
block|,
literal|"        "
block|,
literal|"  __  __ "
block|,
literal|" |  \\/  |"
block|,
literal|" | |\\/| |"
block|,
literal|" | |  | |"
block|,
literal|" |_|  |_|"
block|,
literal|"         "
block|,
literal|"  _____ "
block|,
literal|" | ____|"
block|,
literal|" |  _|  "
block|,
literal|" | |___ "
block|,
literal|" |_____|"
block|,
literal|"        "
block|,
literal|"  _____ "
block|,
literal|" |_   _|"
block|,
literal|"   | |  "
block|,
literal|"   | |  "
block|,
literal|"   |_|  "
block|,
literal|"        "
block|,
literal|"   ___  "
block|,
literal|"  / _ \\ "
block|,
literal|" | | | |"
block|,
literal|" | |_| |"
block|,
literal|"  \\___/ "
block|,
literal|"        "
block|,
literal|"   ___  "
block|,
literal|"  / _ \\ "
block|,
literal|" | | | |"
block|,
literal|" | |_| |"
block|,
literal|"  \\___/ "
block|,
literal|"        "
block|,
literal|"     _    "
block|,
literal|"    / \\   "
block|,
literal|"   / _ \\  "
block|,
literal|"  / ___ \\ "
block|,
literal|" /_/   \\_\\"
block|,
literal|"          "
block|,
literal|"  _  __"
block|,
literal|" | |/ /"
block|,
literal|" | ' / "
block|,
literal|" | . \\ "
block|,
literal|" |_|\\_\\"
block|,
literal|"             "
block|,
literal|" =========== "
block|,
literal|"             "
block|,     }
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|ticks
init|=
name|Iterators
operator|.
name|cycle
argument_list|(
name|message
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

