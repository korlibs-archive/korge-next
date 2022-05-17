package com.soywiz.korge.gradle.targets.ios

import java.io.File
import com.soywiz.korge.gradle.util.*

object IosProjectTools {
    fun genBootstrapKt(entrypoint: String): String = """
        import $entrypoint
        
        @ThreadLocal
        object NewAppDelegate : com.soywiz.korgw.KorgwBaseNewAppDelegate() {
            override fun applicationDidFinishLaunching(app: platform.UIKit.UIApplication) { applicationDidFinishLaunching(app) { ${entrypoint}() } }
        }
    """.trimIndent()

    fun genMainObjC(): String = """
        #import <UIKit/UIKit.h>
        #import <GameMain/GameMain.h>

        @interface AppDelegate : UIResponder <UIApplicationDelegate>
        @property (strong, nonatomic) UIWindow *window;
        @end

        int main(int argc, char * argv[]) {
            @autoreleasepool { return UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class])); }
        }

        @implementation AppDelegate
        - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidFinishLaunchingApp: application];
            return YES;
        }
        - (void)applicationWillResignActive:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillResignActiveApp: application];
        }
        - (void)applicationDidEnterBackground:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidEnterBackgroundApp: application];
        }
        - (void)applicationWillEnterForeground:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillEnterForegroundApp: application];
        }
        - (void)applicationDidBecomeActive:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationDidBecomeActiveApp: application];
        }
        - (void)applicationWillTerminate:(UIApplication *)application {
            [[GameMainNewAppDelegate getNewAppDelegate] applicationWillTerminateApp: application];
        }
        @end
    """.trimIndent()

    fun genLaunchScreenStoryboard(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="no"?>
        <document type="com.apple.InterfaceBuilder3.CocoaTouch.Storyboard.XIB" version="3.0" toolsVersion="13122.16" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" launchScreen="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES" initialViewController="01J-lp-oVM">
            <dependencies>
                <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="13104.12"/>
                <capability name="Safe area layout guides" minToolsVersion="9.0"/>
                <capability name="documents saved in the Xcode 8 format" minToolsVersion="8.0"/>
            </dependencies>
            <scenes>
                <!--View Controller-->
                <scene sceneID="EHf-IW-A2E">
                    <objects>
                        <viewController id="01J-lp-oVM" sceneMemberID="viewController">
                            <view key="view" contentMode="scaleToFill" id="Ze5-6b-2t3">
                                <rect key="frame" x="0.0" y="0.0" width="375" height="667"/>
                                <autoresizingMask key="autoresizingMask" widthSizable="YES" heightSizable="YES"/>
                                <color key="backgroundColor" red="1" green="1" blue="1" alpha="1" colorSpace="custom" customColorSpace="sRGB"/>
                                <viewLayoutGuide key="safeArea" id="6Tk-OE-BBY"/>
                            </view>
                        </viewController>
                        <placeholder placeholderIdentifier="IBFirstResponder" id="iYj-Kq-Ea1" userLabel="First Responder" sceneMemberID="firstResponder"/>
                    </objects>
                    <point key="canvasLocation" x="53" y="375"/>
                </scene>
            </scenes>
        </document>
    """.trimIndent()

    fun prepareKotlinNativeIosProject(folder: File) {
        folder["app/main.m"].ensureParents().writeText(genMainObjC())
        folder["app/Base.lproj/LaunchScreen.storyboard"].ensureParents().writeText(genLaunchScreenStoryboard())
        folder["app/Assets.xcassets/Contents.json"].ensureParents().writeText("""
            {
              "info" : {
                "version" : 1,
                "author" : "xcode"
              }
            }
        """.trimIndent())
        folder["app/Info.plist"].ensureParents().writeText(Indenter {
            line("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            line("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">")
            line("<plist version=\"1.0\">")
            line("<dict>")
            indent {
                line("<key>CFBundleDevelopmentRegion</key>")
                line("<string>$(DEVELOPMENT_LANGUAGE)</string>")
                line("<key>CFBundleExecutable</key>")
                line("<string>$(EXECUTABLE_NAME)</string>")
                line("<key>CFBundleIdentifier</key>")
                line("<string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>")
                line("<key>CFBundleInfoDictionaryVersion</key>")
                line("<string>6.0</string>")
                line("<key>CFBundleName</key>")
                line("<string>$(PRODUCT_NAME)</string>")
                line("<key>CFBundlePackageType</key>")
                line("<string>APPL</string>")
                line("<key>CFBundleShortVersionString</key>")
                line("<string>1.0</string>")
                line("<key>CFBundleVersion</key>")
                line("<string>1</string>")
                line("<key>LSRequiresIPhoneOS</key>")
                line("<true/>")
                line("<key>UILaunchStoryboardName</key>")
                line("<string>LaunchScreen</string>")
                //line("<key>UIMainStoryboardFile</key>")
                //line("<string>Main</string>")
                line("<key>UIRequiredDeviceCapabilities</key>")
                line("<array>")
                indent {
                    line("<string>armv7</string>")
                }
                line("</array>")
                line("<key>UISupportedInterfaceOrientations</key>")
                line("<array>")
                indent {
                    line("<string>UIInterfaceOrientationPortrait</string>")
                    line("<string>UIInterfaceOrientationLandscapeLeft</string>")
                    line("<string>UIInterfaceOrientationLandscapeRight</string>")
                }
                line("</array>")
                line("<key>UISupportedInterfaceOrientations~ipad</key>")
                line("<array>")
                indent {
                    line("<string>UIInterfaceOrientationPortrait</string>")
                    line("<string>UIInterfaceOrientationPortraitUpsideDown</string>")
                    line("<string>UIInterfaceOrientationLandscapeLeft</string>")
                    line("<string>UIInterfaceOrientationLandscapeRight</string>")
                }
                line("</array>")
            }
            line("</dict>")
            line("</plist>")
        })
    }
}
