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
name|segment
operator|.
name|file
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
name|FileOutputStream
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

begin_comment
comment|/**  * Test checks symmetric behaviour with  BufferedReader  * FIXME: this is mostly taken from a copy of org.apache.commons.io.input  * with a fix for IO-471. Replace again once commons-io has released a fixed version.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ReversedLinesReaderTestData
block|{
specifier|private
name|ReversedLinesReaderTestData
parameter_list|()
block|{}
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|WINDOWS_31J_BIN
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|126
block|,
operator|-
literal|97
block|,
operator|-
literal|126
block|,
operator|-
literal|96
block|,
operator|-
literal|126
block|,
operator|-
literal|95
block|,
operator|-
literal|126
block|,
operator|-
literal|94
block|,
operator|-
literal|126
block|,
operator|-
literal|93
block|,
literal|13
block|,
literal|10
block|,
operator|-
literal|106
block|,
operator|-
literal|66
block|,
operator|-
literal|105
block|,
literal|65
block|,
operator|-
literal|114
block|,
literal|113
block|,
operator|-
literal|117
block|,
operator|-
literal|98
block|,
literal|13
block|,
literal|10
block|,     }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|GBK_BIN
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|61
block|,
operator|-
literal|9
block|,
operator|-
literal|35
block|,
operator|-
literal|108
block|,
operator|-
literal|41
block|,
operator|-
literal|45
block|,
operator|-
literal|66
block|,
operator|-
literal|87
block|,
literal|13
block|,
literal|10
block|,
operator|-
literal|68
block|,
operator|-
literal|14
block|,
operator|-
literal|52
block|,
operator|-
literal|27
block|,
operator|-
literal|42
block|,
operator|-
literal|48
block|,
operator|-
literal|50
block|,
operator|-
literal|60
block|,
literal|13
block|,
literal|10
block|,     }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|X_WINDOWS_949_BIN
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|57
block|,
operator|-
literal|47
block|,
operator|-
literal|79
block|,
operator|-
literal|71
block|,
operator|-
literal|66
block|,
operator|-
literal|18
block|,
literal|13
block|,
literal|10
block|,
operator|-
literal|76
block|,
operator|-
literal|21
block|,
operator|-
literal|57
block|,
operator|-
literal|47
block|,
operator|-
literal|71
block|,
operator|-
literal|50
block|,
operator|-
literal|79
block|,
operator|-
literal|71
block|,
literal|13
block|,
literal|10
block|,     }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|X_WINDOWS_950_BIN
init|=
operator|new
name|byte
index|[]
block|{
operator|-
literal|87
block|,
operator|-
literal|6
block|,
operator|-
literal|65
block|,
operator|-
literal|23
block|,
operator|-
literal|92
block|,
literal|108
block|,
operator|-
literal|88
block|,
operator|-
literal|54
block|,
literal|13
block|,
literal|10
block|,
operator|-
literal|63
block|,
literal|99
block|,
operator|-
literal|59
block|,
operator|-
literal|23
block|,
operator|-
literal|92
block|,
operator|-
literal|92
block|,
operator|-
literal|92
block|,
operator|-
literal|27
block|,
literal|13
block|,
literal|10
block|,     }
decl_stmt|;
specifier|public
specifier|static
name|File
name|createFile
parameter_list|(
name|File
name|file
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
finally|finally
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

