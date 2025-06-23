import { inject, Injectable } from '@angular/core';
import { UserResponse } from '../responses/user/user.response';
import { TokenService } from '../service/token.service';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { UserService } from '../service/user.service';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard {
  userResponse?: UserResponse | null;
  constructor(
    private tokenService: TokenService,
    private router: Router,
    private userService: UserService
  ) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const isTokenExpired = this.tokenService.isTokenExpired();
    const userId = this.tokenService.getUserId();
    const isUserIdValid = userId !== null && userId.length > 0;
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
    debugger;
    const isAdmin = this.userResponse?.roleName == 'ADMIN';
    debugger;
    if (!isTokenExpired && isUserIdValid && isAdmin) {
      return true;
    } else {
      debugger;
      this.router.navigate(['/login']);
      return false;
    }
  }
}

export const AdminGuardFn: CanActivateFn = (
  next: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean => {
  debugger;
  return inject(AdminGuard).canActivate(next, state);
};
