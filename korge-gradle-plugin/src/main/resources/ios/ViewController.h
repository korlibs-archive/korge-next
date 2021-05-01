#import <UIKit/UIKit.h>
#import <GLKit/GLKit.h>
#import <GameMain/GameMain.h>
@interface ViewController : GLKViewController
@property(strong, nonatomic) EAGLContext *context;
@property(strong, nonatomic) GameMainMyIosGameWindow2 *gameWindow2;
@property(strong, nonatomic) GameMainRootGameMain *rootGameMain;
@property(strong, nonatomic) NSArray<UITouch*> *touches;
@property boolean_t initialized;
@property boolean_t reshape;
@end
