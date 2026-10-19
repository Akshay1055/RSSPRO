Set WshShell = CreateObject("WScript.Shell")
strDesktop = WshShell.SpecialFolders("Desktop")
Set oShellLink = WshShell.CreateShortcut(strDesktop & "\Android Studio (Auto Cleanup).lnk")
oShellLink.TargetPath = "D:\Project_work\RSS_mob\master\swayamsevak_android\run_studio_with_auto_cleanup.bat"
oShellLink.WorkingDirectory = "D:\Project_work\RSS_mob\master\swayamsevak_android"
oShellLink.WindowStyle = 1
oShellLink.Description = "Launch Android Studio with automatic background cleanup on exit"
oShellLink.Save
